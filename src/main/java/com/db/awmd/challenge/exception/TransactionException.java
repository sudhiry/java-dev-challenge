package com.db.awmd.challenge.exception;

/**
 * This exception represents transaction error for any transfer that happens
 */
public class TransactionException extends RuntimeException {

    public TransactionException(String message) { super(message); }
}
