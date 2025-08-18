package in.vijay.bank.entity;


import lombok.Getter;

@Getter
public enum AccountType {
    SAVINGS(0.04),  // 4% interest rate
    CURRENT(0.0);    // No interest for current accounts

    private final double interestRate;

    AccountType(double interestRate) {
        this.interestRate = interestRate;
    }

}