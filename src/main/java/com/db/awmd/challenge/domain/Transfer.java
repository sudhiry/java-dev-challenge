package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * This class represent the transaction request from web level.
 */

@Data
public class Transfer {

    /**
     * The account id from which amount should be transferred
     */
    @NotNull
    @NotEmpty
    private final String accountFromId;

    /**
     * The account id to which amount should be transferred
     */
    @NotNull
    @NotEmpty
    private final String accountToId;

    /**
     * The amount which should be transferred.
     * Should be positive value.
     */
    @NotNull
    @Min(value = 1, message = "Transfer amount must be positive.")
    private BigDecimal amount;

    @JsonCreator
    public Transfer(@JsonProperty("accountFromId") String accountFromId,
                    @JsonProperty("accountToId") String accountToId,
                    @JsonProperty("amount") BigDecimal amount) {
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
    }
}
