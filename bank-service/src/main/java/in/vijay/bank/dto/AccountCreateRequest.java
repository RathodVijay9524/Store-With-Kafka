package in.vijay.bank.dto;

import in.vijay.bank.entity.AccountType;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateRequest {

    private String userId;
    private AccountType accountType;
    private BigDecimal initialBalance;
}