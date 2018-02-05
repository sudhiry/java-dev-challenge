package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.InsufficientMoneyTransactionException;
import com.db.awmd.challenge.exception.NegativeAmountTransactionException;
import com.db.awmd.challenge.exception.NoAccountExistsException;
import com.db.awmd.challenge.exception.SameAccountTransactionException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * This class is actual service which manages the transactions checking validations and throwing appropriate exceptions.
 */

@Service
@Slf4j
public class TransferService {

    @Getter
    private final AccountsRepository accountsRepository;

    @Getter
    private final NotificationService notificationService;

    @Autowired
    public TransferService(AccountsRepository accountsRepository, NotificationService notificationService) {
        this.accountsRepository = accountsRepository;
        this.notificationService = notificationService;
    }

    /**
     * This method does transfers in batch.
     *
     * @param transfers {@link com.db.awmd.challenge.domain.Transfer}
     */
    public void transfer(List<Transfer> transfers) {
        transfers.stream().forEach(this::transfer);
    }

    /**
     * This method actually transfers the amount from one account to another.
     * This method also checks validations and throws appropriate errors.
     *
     * @param transfer {@link com.db.awmd.challenge.domain.Transfer}
     */
    public void transfer(Transfer transfer) {

        Account fromAccount = accountsRepository.getAccount(transfer.getAccountFromId());
        Account toAccount = accountsRepository.getAccount(transfer.getAccountToId());
        BigDecimal amount  = transfer.getAmount();

        if(null == fromAccount) {
            throw new NoAccountExistsException("Account " + transfer.getAccountFromId() + " do not exists");
        }

        if(null == toAccount) {
            throw new NoAccountExistsException("Account " + transfer.getAccountToId() + " do not exists");
        }

        this.validateTransfer(fromAccount, toAccount, amount);

        log.debug("Updating accounts for transaction in accounts {} and {}", fromAccount, toAccount);
        boolean transferComplete = accountsRepository.updateAccountsForTransactions(Arrays.asList(
                // Reduce amount from account
                new Transaction(transfer.getAccountFromId(), transfer.getAmount().negate()),
                // Add amount to account
                new Transaction(transfer.getAccountToId(), transfer.getAmount())));

        if(transferComplete) {
            log.debug("Sending notification for transaction to accounts {} and {}", fromAccount, toAccount);
            notificationService.notifyAboutTransfer(fromAccount,
                    "Amount "+ amount + " is transferred to account ID " + toAccount.getAccountId());
            notificationService.notifyAboutTransfer(toAccount,
                    "Amount " + amount + " is transferred from account ID " + fromAccount.getAccountId());
        }
    }

    /**
     * This method returns true if the transaction is valid.
     * For valid transaction the amount should be positive and the account for transfers must not be same.
     *
     * @param fromAccount
     * @param toAccount
     * @param amount
     * @return boolean: if the transfer is valid or not
     * @throws {@link com.db.awmd.challenge.exception.SameAccountTransactionException}
     * @throws {@link com.db.awmd.challenge.exception.InsufficientMoneyTransactionException}
     * @throws {@link com.db.awmd.challenge.exception.NegativeAmountTransactionException}
     */
    protected boolean validateTransfer(Account fromAccount, Account toAccount, BigDecimal amount)
            throws SameAccountTransactionException, InsufficientMoneyTransactionException, NegativeAmountTransactionException{

        log.debug("Validating accounts for transaction {} and {}", fromAccount, toAccount);

        if(fromAccount.getAccountId().equals(toAccount.getAccountId())) {
            throw new SameAccountTransactionException("Can not transfer amount to same account");
        }

        if(!(fromAccount.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) >= 0)){
            throw new InsufficientMoneyTransactionException("Insufficient money to transfer");
        }
        if(!(amount.compareTo(BigDecimal.ZERO) >= 0)){
            throw new NegativeAmountTransactionException("Cannot transfer negative amount");
        }

        return true;
    }
}
