package com.shohidulhaque.domain.model;

import java.util.Objects;

/**
 * A representation of an entity that owns an {@link Account}.
 */
public class AccountHolder {

    /**
     * An internal repository id for an account holder.
     */
    private long id;

    /**
     * A unique id for every entity that owns an account.
     */
    private String accountHolderId;

    private String firstName;

    private String lastName;


    public AccountHolder(String accountHolderId, String firstName, String lastName) {
        this.id = -1;
        this.accountHolderId = accountHolderId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public AccountHolder(long id, String accountHolderId, String firstName, String lastName) {
        this.id = id;
        this.accountHolderId = accountHolderId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getAccountHolderId() {
        return accountHolderId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "AccountHolder{" +
                "id=" + id +
                ", accountHolderId='" + accountHolderId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountHolder that = (AccountHolder) o;
        return id == that.id &&
                Objects.equals(accountHolderId, that.accountHolderId) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountHolderId, firstName, lastName);
    }

    public void setId(long id) { this.id = id; }
    public long getId() { return id; }
}
