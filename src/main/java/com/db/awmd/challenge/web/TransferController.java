package com.db.awmd.challenge.web;


import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.InsufficientMoneyTransactionException;
import com.db.awmd.challenge.exception.NegativeAmountTransactionException;
import com.db.awmd.challenge.exception.NoAccountExistsException;
import com.db.awmd.challenge.exception.SameAccountTransactionException;
import com.db.awmd.challenge.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * This class is transfer controller. This class handles the REST requests for all of the transfers and its validations
 */

@RestController
@RequestMapping("/v1/transfer")
@Slf4j
public class TransferController {

    private final TransferService transferService;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * This method is REST implementation of POST method, which transfers the amount from one account to another
     * @param transfer {@link com.db.awmd.challenge.domain.Transfer}
     * @return HTTP RESPONSE
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createAccount(@RequestBody @Valid Transfer transfer) {
        log.info("Making transfer {}", transfer);

        try {
            this.transferService.transfer(transfer);
        }catch (NoAccountExistsException ex) {
            log.error("Error in making transfer {}", ex.getMessage(),ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InsufficientMoneyTransactionException ex) {
            log.error("Error in making transfer {}", ex.getMessage(),ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.PRECONDITION_FAILED);
        } catch (SameAccountTransactionException |NegativeAmountTransactionException ex) {
            log.error("Error in making transfer {}", ex.getMessage(),ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
