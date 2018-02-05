package com.db.awmd.challenge.exception;

/**
 * This exception represents the account do not exists
 */
public class NoAccountExistsException extends RuntimeException {

    public NoAccountExistsException(String message) {
        super(message);
    }
}
