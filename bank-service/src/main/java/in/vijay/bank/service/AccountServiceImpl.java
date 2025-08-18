package in.vijay.bank.service;

import in.vijay.bank.client.service.IdGeneratorClient;
import in.vijay.bank.client.service.UserHttpClient;
import in.vijay.bank.dto.*;
import in.vijay.bank.entity.*;
import in.vijay.bank.exceptions.AccountNotActiveException;
import in.vijay.bank.exceptions.AccountNotFoundException;
import in.vijay.bank.exceptions.CurrencyMismatchException;
import in.vijay.bank.exceptions.InsufficientFundsException;
import in.vijay.bank.repository.AccountRepository;
import in.vijay.bank.repository.TransactionRepository;
import in.vijay.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserHttpClient userHttpClient;
    private final IdGeneratorClient idGeneratorClient;
    private final ModelMapper mapper;
    private final TransactionTemplate transactionTemplate;
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public AccountResponse createAccount(AccountCreateRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String accountNumber = idGeneratorClient.generateId("ACCOUNT", "SBI", 9);
            String accountId = idGeneratorClient.generateDateBasedId("Account", "ACC");

            UserResponse user = userHttpClient.getUserById(request.getUserId());

            return transactionTemplate.execute(status -> {
                if (request.getAccountType() == AccountType.SAVINGS &&
                        request.getInitialBalance().compareTo(new BigDecimal("1000")) < 0) {
                    throw new IllegalArgumentException("Minimum balance for savings account is 1000");
                }

                Account account = Account.builder()

                        .userId(user.getId())
                        .accountNumber(accountNumber)
                        .accountType(request.getAccountType())
                        .currency("INR")
                        .balance(request.getInitialBalance())
                        .status(AccountStatus.ACTIVE)
                        .build();
                  account.setId(accountId);
                Account savedAccount = accountRepository.save(account);

                CompletableFuture.runAsync(() ->
                                transactionTemplate.executeWithoutResult(txStatus -> {
                                    buildTransaction(null, savedAccount, request.getInitialBalance(),
                                            "Account opening", TransactionStatus.COMPLETED);
                                }),
                        virtualThreadExecutor
                );

                return mapper.map(savedAccount, AccountResponse.class);
            });
        }, virtualThreadExecutor).join();
    }

    @Override
    public TransactionResponse deposit(String accountId, TransactionRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            return transactionTemplate.execute(status -> {
                if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Deposit amount must be positive");
                }

                Account account = accountRepository.findByAccountNumberWithLock(accountId)
                        .orElseThrow(() -> new AccountNotFoundException("Account not found"));

                account.setBalance(account.getBalance().add(request.getAmount()));
                accountRepository.save(account);

                return mapper.map(
                        buildTransaction(null, account, request.getAmount(),
                                "Cash deposit", TransactionStatus.COMPLETED),
                        TransactionResponse.class
                );
            });
        }, virtualThreadExecutor).join();
    }

    @Override
    public TransactionResponse withdraw(String accountId, TransactionRequest request) throws InsufficientFundsException {
        return CompletableFuture.supplyAsync(() -> {
            return transactionTemplate.execute(status -> {
                if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Withdrawal amount must be positive");
                }

                Account account = accountRepository.findByAccountNumberWithLock(accountId)
                        .orElseThrow(() -> new AccountNotFoundException("Account not found"));

                BigDecimal minimumBalance = account.getAccountType() == AccountType.SAVINGS
                        ? new BigDecimal("1000")
                        : BigDecimal.ZERO;

                if (account.getBalance().subtract(request.getAmount()).compareTo(minimumBalance) < 0) {
                    try {
                        throw new InsufficientFundsException(
                                "Withdrawal would violate minimum balance requirement of " + minimumBalance
                        );
                    } catch (InsufficientFundsException e) {
                        throw new RuntimeException(e);
                    }
                }

                account.setBalance(account.getBalance().subtract(request.getAmount()));
                accountRepository.save(account);

                return mapper.map(
                        buildTransaction(account, null, request.getAmount(),
                                "Cash withdrawal", TransactionStatus.COMPLETED),
                        TransactionResponse.class
                );
            });
        }, virtualThreadExecutor).join();
    }


    @Override
    public TransactionResponse transfer(TransferRequest request) throws InsufficientFundsException {
        return CompletableFuture.supplyAsync(() -> {
            return transactionTemplate.execute(status -> {
                // All DB operations now run within the same transaction
                Account fromAccount = accountRepository.findByAccountNumberWithLock(request.getFromAccountId())
                        .orElseThrow(() -> new AccountNotFoundException("From account not found"));

                Account toAccount = accountRepository.findByAccountNumberWithLock(request.getToAccountId())
                        .orElseThrow(() -> new AccountNotFoundException("To account not found"));

                // Validate transfer
                if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Transfer amount must be positive");
                }

                // Check minimum balance requirement
                BigDecimal minimumBalance = fromAccount.getAccountType() == AccountType.SAVINGS
                        ? new BigDecimal("1000")
                        : BigDecimal.ZERO;

                if (fromAccount.getBalance().subtract(request.getAmount()).compareTo(minimumBalance) < 0) {
                    try {
                        throw new InsufficientFundsException(
                                "Transfer would violate minimum balance requirement of " + minimumBalance
                        );
                    } catch (InsufficientFundsException e) {
                        throw new RuntimeException(e);
                    }
                }

                // Perform transfer
                fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
                toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);

                // Create and return transaction
                return mapper.map(
                        buildTransaction(fromAccount, toAccount, request.getAmount(),
                                "Transfer", TransactionStatus.COMPLETED),
                        TransactionResponse.class
                );
            });
        }, virtualThreadExecutor).join();
    }


    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccount(String accountId) {
        return CompletableFuture.supplyAsync(() -> {
            Account account = accountRepository.findByAccountNumber(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));
            return mapper.map(account, AccountResponse.class);
        }, virtualThreadExecutor).join();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getUserAccounts(String userId) {
        return CompletableFuture.supplyAsync(() ->
                        accountRepository.findByUserId(userId).stream()
                                .map(acc -> mapper.map(acc, AccountResponse.class))
                                .collect(Collectors.toList()),
                virtualThreadExecutor
        ).join();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByAccountId(String accountId) {
        return CompletableFuture.supplyAsync(() -> {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));

            return transactionRepository.findByAccountIdOrRelatedAccountId(accountId, accountId)
                    .stream()
                    .map(transaction -> mapper.map(transaction, TransactionResponse.class))
                    .collect(Collectors.toList());
        }, virtualThreadExecutor).join();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByAccountNumber(String accountNumber) {
        return CompletableFuture.supplyAsync(() -> {
            Account account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

            return transactionRepository.findByAccountIdOrRelatedAccountId(account.getId(), account.getId())
                    .stream()
                    .map(transaction -> mapper.map(transaction, TransactionResponse.class))
                    .collect(Collectors.toList());
        }, virtualThreadExecutor).join();
    }

    @Override
    public BigDecimal calculateInterest(String accountId) {
        // Implementation would use virtual threads if doing I/O
        return null;
    }

    @Override
    public TransactionResponse applyInterest(String accountId) {
        // Implementation would use virtual threads if doing I/O
        return null;
    }

    private Transaction buildTransaction(Account from, Account to, BigDecimal amount,
                                         String description, TransactionStatus status) {
        String transactionId = idGeneratorClient.generateId("TRANSACTION", "TRI", 4);

        Transaction transaction = Transaction.builder()

                .fromAccount(from != null ? from.getAccountNumber() : null)
                .toAccount(to != null ? to.getAccountNumber() : null)
                .amount(amount)
                .currency("INR")
                .description(description)
                .status(status)
                .account(from)
                .relatedAccount(to)
                .build();
transaction.setId(transactionId);
        return transactionRepository.save(transaction);
    }
}