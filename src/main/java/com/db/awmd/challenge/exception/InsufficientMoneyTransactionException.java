package com.db.awmd.challenge.exception;

/**
 * This exception represents insufficient amount present in the account to transfer the money
 */
public class InsufficientMoneyTransactionException extends TransactionException {

    public InsufficientMoneyTransactionException(String message) {
        super(message);
    }
}
