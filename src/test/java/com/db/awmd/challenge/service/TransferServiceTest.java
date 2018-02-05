package com.db.awmd.challenge.service;

import com.db.awmd.challenge.TestConfigurations;
import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.InsufficientMoneyTransactionException;
import com.db.awmd.challenge.exception.NegativeAmountTransactionException;
import com.db.awmd.challenge.exception.NoAccountExistsException;
import com.db.awmd.challenge.exception.SameAccountTransactionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfigurations.class)
public class TransferServiceTest {

    @Autowired
    private AccountsService accountsService;

    @Autowired
    protected TransferService transferService;

    @Autowired
    protected NotificationService notificationService;

    /**
     * Test for transferring the amount from one account to another.
     *
     * @throws Exception
     */
    @Test
    public void transfer() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountFrom = new Account(accountIdFrom, new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);

        Account accountTo = new Account(accountIdTo, new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);

        Transfer transfer = new Transfer(accountIdFrom, accountIdTo, new BigDecimal(333));
        this.transferService.transfer(transfer);

        assertThat(this.accountsService.getAccount(accountIdFrom).getBalance()).isEqualTo(new BigDecimal(667));
        assertThat(this.accountsService.getAccount(accountIdTo).getBalance()).isEqualTo(new BigDecimal(1333));

        // Verify notifications
        Mockito.verify(this.notificationService,Mockito.times(1))
                .notifyAboutTransfer(accountFrom, "Amount "
                        + new BigDecimal(333) + " is transferred to account ID " + accountIdTo);

        Mockito.verify(this.notificationService,Mockito.times(1))
                .notifyAboutTransfer(accountTo, "Amount "
                        + new BigDecimal(333) + " is transferred from account ID " + accountIdFrom);

    }

    /**
     * Test for transferring the amount from one account to another when to account do not exists.
     *
     * @throws Exception
     */
    @Test
    public void transfer_noToAccountExists() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountFrom = new Account(accountIdFrom, new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);

        try {
            this.transferService.transfer(new Transfer(accountIdFrom, accountIdTo, new BigDecimal(333)));
            fail("Should have failed when to account is not present");
        } catch (NoAccountExistsException ex) {
            assertThat(ex.getMessage()).isEqualTo("Account " + accountIdTo + " do not exists");
            assertThat(this.accountsService.getAccount(accountIdFrom).getBalance()).isEqualTo(new BigDecimal(1000));
        }
    }

    /**
     * Test for transferring the amount from one account to another when from account do not exists.
     *
     * @throws Exception
     */
    @Test
    public void transfer_noFromAccountExists() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountTo = new Account(accountIdTo, new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);

        try {
            Transfer transfer = new Transfer(accountIdFrom, accountIdTo, new BigDecimal(333));
            this.transferService.transfer(transfer);
            fail("Should have failed when From account is not present");
        } catch (NoAccountExistsException ex) {
            assertThat(ex.getMessage()).isEqualTo("Account " + accountIdFrom + " do not exists");
            assertThat(this.accountsService.getAccount(accountIdTo).getBalance()).isEqualTo(new BigDecimal(1000));
        }
    }

    /**
     * Test for transferring the amount to the same amount.
     *
     * @throws Exception
     */
    @Test
    public void transfer_SameAccount() throws Exception {
        String accountId  = UUID.randomUUID().toString();

        Account account = new Account(accountId, new BigDecimal(1000));
        this.accountsService.createAccount(account);

        try {
            Transfer transfer = new Transfer(accountId, accountId, new BigDecimal(333));
            this.transferService.transfer(transfer);
            fail("Should have failed when transfer is to same account");
        } catch (SameAccountTransactionException ex) {
            assertThat(ex.getMessage()).isEqualTo("Can not transfer amount to same account");
            assertThat(this.accountsService.getAccount(accountId).getBalance()).isEqualTo(new BigDecimal(1000));
        }
    }

    /**
     * Test for transferring the amount from one account to another when from account has insufficient money
     *
     * @throws Exception
     */
    @Test
    public void transfer_InsufficientMoney() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountFrom = new Account(accountIdFrom, new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);

        Account accountTo = new Account(accountIdTo, new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);

        try {
            Transfer transfer = new Transfer(accountIdFrom, accountIdTo, new BigDecimal(1020));
            this.transferService.transfer(transfer);
            fail("Should have failed when From account do not have enough money to transfer");
        } catch (InsufficientMoneyTransactionException ex) {
            assertThat(ex.getMessage()).isEqualTo("Insufficient money to transfer");
            assertThat(this.accountsService.getAccount(accountIdFrom).getBalance()).isEqualTo(new BigDecimal(1000));
            assertThat(this.accountsService.getAccount(accountIdTo).getBalance()).isEqualTo(new BigDecimal(1000));
        }
    }

    /**
     * Test for transferring the amount from one account to another in batch/parallel
     *
     * @throws Exception
     */
    @Test
    public void transfer_Multiple() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountFrom = new Account(accountIdFrom, new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);

        Account accountTo = new Account(accountIdTo, new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);

        this.transferService.transfer(Arrays.asList(
                new Transfer(accountIdFrom, accountIdTo, new BigDecimal(286)),
                new Transfer(accountIdTo, accountIdFrom, new BigDecimal(12)),
                new Transfer(accountIdFrom, accountIdTo, new BigDecimal(333)),
                new Transfer(accountIdTo, accountIdFrom, new BigDecimal(222))
        ));

        assertThat(this.accountsService.getAccount(accountIdFrom).getBalance()).isEqualTo(new BigDecimal(615));
        assertThat(this.accountsService.getAccount(accountIdTo).getBalance()).isEqualTo(new BigDecimal(1385));
    }

    /**
     * Test for transferring the negative amount from one account to another
     *
     * @throws Exception
     */
    @Test
    public void transfer_Negative() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountFrom = new Account(accountIdFrom, new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);

        Account accountTo = new Account(accountIdTo, new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);

        try {
            this.transferService.transfer(new Transfer(accountIdFrom, accountIdTo, new BigDecimal(-112)));
            fail("Should have failed when transfer amount is negative");
        } catch (NegativeAmountTransactionException ex) {
            assertThat(ex.getMessage()).isEqualTo("Cannot transfer negative amount");
            assertThat(this.accountsService.getAccount(accountIdFrom).getBalance()).isEqualTo(new BigDecimal(1000));
            assertThat(this.accountsService.getAccount(accountIdTo).getBalance()).isEqualTo(new BigDecimal(1000));
        }
    }
}
