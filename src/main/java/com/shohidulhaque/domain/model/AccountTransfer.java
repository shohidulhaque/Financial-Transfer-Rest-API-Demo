package com.shohidulhaque.domain.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * A entity object representing a financial transaction that will happen or has already had been performed and persisted
 * to a repository.
 */
public class AccountTransfer {

    /**
     * An internal repository id for a transaction.
     */
    long id;

    BigDecimal balance;

    long fromAccountId;

    long toAccountId;

    Date transactionTime;

    public AccountTransfer(long id, BigDecimal balance, long fromAccountId, long toAccountId, Date transactionTime) {
        this.id = id;
        this.balance = balance;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.transactionTime = transactionTime;
    }


}
