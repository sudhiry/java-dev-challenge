package com.db.awmd.challenge.exception;

/**
 * This exception represents the amount cannot be transferred to the same amount
 */
public class SameAccountTransactionException extends TransactionException {

    public SameAccountTransactionException(String message) {
        super(message);
    }
}
