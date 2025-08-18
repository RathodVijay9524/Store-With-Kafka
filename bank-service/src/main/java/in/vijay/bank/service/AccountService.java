package in.vijay.bank.service;



import in.vijay.bank.dto.*;
import in.vijay.bank.exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    AccountResponse createAccount(AccountCreateRequest request);

    TransactionResponse deposit(String accountId, TransactionRequest request);

    TransactionResponse withdraw(String accountId, TransactionRequest request) throws InsufficientFundsException;

    TransactionResponse transfer(TransferRequest request) throws InsufficientFundsException;

    AccountResponse getAccount(String accountId);

    List<AccountResponse> getUserAccounts(String userId);
    List<TransactionResponse> getTransactionsByAccountId(String accountId);

    List<TransactionResponse> getTransactionsByAccountNumber(String accountNumber);

    BigDecimal calculateInterest(String accountId);

    TransactionResponse applyInterest(String accountId);
}