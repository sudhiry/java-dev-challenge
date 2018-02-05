package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import java.util.List;

public interface AccountsRepository {

  /**
   * This method creates account in the database
   * @param account
   * @throws DuplicateAccountIdException
   */
  void createAccount(Account account) throws DuplicateAccountIdException;

  /**
   * This method returns the account details for the perticular account id
   * @param accountId
   * @return
   */
  Account getAccount(String accountId);

  /**
   * This method removes/clears the accounts from the database
   */
  void clearAccounts();

  /**
   * This method updates the accounts with given amount.
   *
   * @param transactions {@link com.db.awmd.challenge.domain.Transaction}
   * @return boolean, returns true if amount updating is successful
   */
  boolean updateAccountsForTransactions(List<Transaction> transactions);
}
