package com.db.awmd.challenge.exception;

/**
 * This exception represents negative amount cannot be transferred to any other account
 */
public class NegativeAmountTransactionException extends TransactionException {

    public NegativeAmountTransactionException(String message) {
        super(message);
    }
}
