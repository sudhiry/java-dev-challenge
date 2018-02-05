package com.db.awmd.challenge.exception;

/**
 * This exception represents duplicate account already exists in the data
 */
public class DuplicateAccountIdException extends RuntimeException {

  public DuplicateAccountIdException(String message) {
    super(message);
  }
}
