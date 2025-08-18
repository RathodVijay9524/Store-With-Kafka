package in.vijay.bank.controller;
import in.vijay.bank.dto.TransactionResponse;
import in.vijay.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final AccountService accountService;

    // Get transactions by Account ID
    @GetMapping("/account/{accountId}")
    public List<TransactionResponse> getTransactionsByAccountId(@PathVariable String accountId) {
        return accountService.getTransactionsByAccountId(accountId);
    }

    // Get transactions by Account Number
    @GetMapping("/account-number/{accountNumber}")
    public List<TransactionResponse> getTransactionsByAccountNumber(@PathVariable String accountNumber) {
        return accountService.getTransactionsByAccountNumber(accountNumber);
    }
}
