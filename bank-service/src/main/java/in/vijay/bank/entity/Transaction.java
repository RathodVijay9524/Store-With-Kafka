package in.vijay.bank.entity;


import in.vijay.exceptions.BadApiRequestException;
import in.vijay.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Transaction extends BaseEntity<String> {

    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String currency;
    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    // Many-to-One: Many transactions can belong to one account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // For transfers only - the receiving account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_account_id")
    private Account relatedAccount;

}

