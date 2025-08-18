package in.vijay.bank.dto;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransferRequest {

    private String fromAccountId;


    private String toAccountId;


    private BigDecimal amount;
}