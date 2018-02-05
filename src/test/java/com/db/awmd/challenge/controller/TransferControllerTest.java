package com.db.awmd.challenge.controller;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import com.db.awmd.challenge.TestConfigurations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfigurations.class)
@WebAppConfiguration
public class TransferControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    protected NotificationService notificationService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void prepareMockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
        // Reset the existing accounts before each test.
        this.accountsService.getAccountsRepository().clearAccounts();
    }

    @Test
    public void transfer() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountFrom = new Account(accountIdFrom, new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);

        Account accountTo = new Account(accountIdTo, new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);

        this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"" + accountIdFrom+"\",\"accountToId\":\""+accountIdTo+"\",\"amount\":271}"))
                .andExpect(status().isOk());

        Mockito.verify(this.notificationService,Mockito.times(1))
                .notifyAboutTransfer(accountFrom, "Amount "
                        + new BigDecimal(271) + " is transferred to account ID " + accountIdTo);

        Mockito.verify(this.notificationService,Mockito.times(1))
                .notifyAboutTransfer(accountTo, "Amount "
                        + new BigDecimal(271) + " is transferred from account ID " + accountIdFrom);
    }

    @Test
    public void transfer_NoFromAccount() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountTo = new Account(accountIdTo, new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);

        this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"" + accountIdFrom+"\",\"accountToId\":\""+accountIdTo+"\",\"amount\":226}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void transfer_NoToAccount() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountTo = new Account(accountIdTo, new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);

        this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"" + accountIdFrom+"\",\"accountToId\":\""+accountIdTo+"\",\"amount\":21}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void transfer_NegativeAmount() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountFrom = new Account(accountIdFrom, new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);

        Account accountTo = new Account(accountIdTo, new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);

        this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"" + accountIdFrom+"\",\"accountToId\":\""+accountIdTo+"\",\"amount\":-221}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void transfer_SameAccount() throws Exception {
        String accountId  = UUID.randomUUID().toString();

        Account account = new Account(accountId, new BigDecimal(1000));
        this.accountsService.createAccount(account);

        this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"" + accountId+"\",\"accountToId\":\""+accountId+"\",\"amount\":221}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void transfer_InsufficientMoney() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountFrom = new Account(accountIdFrom, new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);

        Account accountTo = new Account(accountIdTo, new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);

        this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"" + accountIdFrom+"\",\"accountToId\":\""+accountIdTo+"\",\"amount\":2021}"))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void transfer_Multiple() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountFrom = new Account(accountIdFrom, new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);

        Account accountTo = new Account(accountIdTo, new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);

        this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"" + accountIdFrom+"\",\"accountToId\":\""+accountIdTo+"\",\"amount\":261}"))
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"" + accountIdTo+"\",\"accountToId\":\""+accountIdFrom+"\",\"amount\":21}"))
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"" + accountIdFrom+"\",\"accountToId\":\""+accountIdTo+"\",\"amount\":421}"))
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"" + accountIdTo+"\",\"accountToId\":\""+accountIdFrom+"\",\"amount\":312}"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/v1/accounts/" + accountIdFrom))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"accountId\":\"" + accountIdFrom + "\",\"balance\":651}"));

        this.mockMvc.perform(get("/v1/accounts/" + accountIdTo))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"accountId\":\"" + accountIdTo + "\",\"balance\":1349}"));
    }

    @Test
    public void transfer_EmptyBody() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountFrom = new Account(accountIdFrom, new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);

        Account accountTo = new Account(accountIdTo, new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);

        this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void transfer_ZeroBalance() throws Exception {
        String accountIdFrom  = UUID.randomUUID().toString();
        String accountIdTo = UUID.randomUUID().toString();

        Account accountFrom = new Account(accountIdFrom, new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);

        Account accountTo = new Account(accountIdTo, new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);

        this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"" + accountIdFrom+"\",\"accountToId\":\""+accountIdTo+"\",\"amount\":1000}"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/v1/accounts/" + accountIdFrom))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"accountId\":\"" + accountIdFrom + "\",\"balance\":0}"));

        this.mockMvc.perform(get("/v1/accounts/" + accountIdTo))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"accountId\":\"" + accountIdTo + "\",\"balance\":2000}"));
    }

}
