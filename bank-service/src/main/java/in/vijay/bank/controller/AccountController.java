package in.vijay.bank.controller;

import in.vijay.bank.dto.*;
import in.vijay.bank.exceptions.InsufficientFundsException;
import in.vijay.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountCreateRequest request) {
        return ResponseEntity.ok(accountService.createAccount(request));
    }

    /**
     * Deposit money into an account by account number
     */
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable String accountNumber,
            @RequestBody TransactionRequest request) {

        TransactionResponse response = accountService.deposit(accountNumber, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Withdraw money from an account by account number
     */
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable String accountNumber,
            @RequestBody TransactionRequest request) throws InsufficientFundsException {

        TransactionResponse response = accountService.withdraw(accountNumber, request);
        return ResponseEntity.ok(response);
    }
    // Transfer money between accounts
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@RequestBody TransferRequest request) throws InsufficientFundsException {
        return ResponseEntity.ok(accountService.transfer(request));
    }

    // Get single account by account number
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(accountService.getAccount(accountId));
    }

    // Get all accounts for a given user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponse>> getUserAccounts(@PathVariable String userId) {
        return ResponseEntity.ok(accountService.getUserAccounts(userId));
    }


}
