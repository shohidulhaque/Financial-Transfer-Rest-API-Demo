package com.shohidulhaque.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * A financial transaction.
 */
public class AccountTransferVO {

    @JsonProperty(required = true)
    BigDecimal amount;

    ;
    @JsonProperty(required = true)
    String fromAccountNumber;
    @JsonProperty
    String toAccountNumber;
    @JsonProperty
    Date transactionTime;

    /*needed for the json mapping libraries used.*/
    public AccountTransferVO() {
    }

    public AccountTransferVO(BigDecimal amount, String fromAccountNumber, String toAccountNumber, Date transactionTime) {
        this.amount = amount;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.transactionTime = transactionTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

}
