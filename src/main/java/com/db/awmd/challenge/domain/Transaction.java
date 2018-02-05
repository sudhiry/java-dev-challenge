package com.db.awmd.challenge.domain;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * This class represents transaction for each account.
 */
@Data
public class Transaction {

    /**
     * Account id on which the transaction to happen
     */
    @NotNull
    @NotEmpty(message = "Account id must not be empty")
    private final String accountId;

    /**
     * The amount which should be added to the account.
     */
    @NotNull(message = "Transaction amount must be present")
    private final BigDecimal amount;

    public Transaction(String accountId, BigDecimal amount) {
        this.accountId = accountId;
        this.amount = amount;
    }
}
