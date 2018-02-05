package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@Slf4j
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  /**
   * Creates the account by calling repository.
   *
   * @param account {@link com.db.awmd.challenge.domain.Account}
   */
  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  /**
   * Returns the account details for given account id
   *
   * @param accountId
   * @return {@link com.db.awmd.challenge.domain.Account}
   */
  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

}
