package in.vijay.bank.dto;

import in.vijay.bank.entity.AccountStatus;
import in.vijay.bank.entity.AccountType;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private String id;
    private String accountNumber;
    private String userId;
    private BigDecimal balance;
    private String currency;
    private AccountType accountType;
    private AccountStatus status;
}
