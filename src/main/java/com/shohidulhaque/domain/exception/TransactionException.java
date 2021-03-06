package com.shohidulhaque.domain.exception;

import java.math.BigDecimal;

/**
 * Represents errors during a transaction.
 */
public class TransactionException extends Exception {

    final String responseCode;
    final long fromId;
    final String fromAccountNumber;
    final long toId;
    final String toAccountNumber;
    final BigDecimal amount;
    public TransactionException(String msg, String responseCode) {
        this(msg, responseCode, -1, null, -1, null, null, null);
    }

    public TransactionException(String msg, String responseCode, String fromAccountNumber,  String toAccountNumber, BigDecimal amount) {
        this(msg, responseCode, -1, fromAccountNumber, -1, toAccountNumber, amount, null);
    }

    public TransactionException(String msg, String responseCode, Throwable cause) {
        this(msg, responseCode, -1, null, -1, null, null, cause);
    }

    public TransactionException(String msg, String responseCode, long fromId, String fromAccountNumber, long toId, String toAccountNumber, BigDecimal amount) {

        this(msg, responseCode, fromId, fromAccountNumber, toId, toAccountNumber, amount, null);
    }

    public TransactionException(String msg, String responseCode, long fromId, String fromAccountNumber, long toId, String toAccountNumber, BigDecimal amount, Throwable cause) {
        super(msg, cause);
        this.responseCode = responseCode;
        this.fromId = fromId;
        this.fromAccountNumber = fromAccountNumber;
        this.toId = toId;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public long getFromId() {
        return fromId;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public long getToId() {
        return toId;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public enum ResponseCode {
        SUCCESS, FAILURE
    }
}
