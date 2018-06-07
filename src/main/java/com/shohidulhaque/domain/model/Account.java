package com.shohidulhaque.domain.model;

import java.math.BigDecimal;

/**
 * A financial account.
 */
public class Account {

    /**
     * An internal repository id for an account.
     */
    private long id;

    /**
     * The entity that owns this account.
     */
    private long accountHolderId;

    /**
     * The unique account number.
     */
    private String accountNumber;

    /**
     * Accounts sort code.
     */
    private String sortCode;

    /**
     * Current balance of bank.
     */
    private BigDecimal balance;

    public Account() {
    }

    public Account(String accountNumber, String sortCode, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.balance = balance;
    }

    public Account(long accountHolderId, String accountNumber, String sortCode, BigDecimal balance) {
        this(-1, accountHolderId, accountNumber, sortCode, balance);
    }

    public Account(long accountId, long accountHolderId, String accountNumber, String sortCode, BigDecimal balance) {
        this.id = accountId;
        this.accountHolderId = accountHolderId;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public long getAccountHolderId() {
        return accountHolderId;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountHolderId=" + accountHolderId +
                ", accountNumber='" + accountNumber + '\'' +
                ", sortCode='" + sortCode + '\'' +
                ", balance=" + balance +
                '}';
    }
}
