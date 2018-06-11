package com.shohidulhaque.domain.repository;

import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.model.Account;
import com.shohidulhaque.domain.service.TransferAccountBalanceResponse;
import com.shohidulhaque.domain.valueobject.UserTransactionVO;

import java.math.BigDecimal;
import java.util.List;

public interface AccountRepository extends  CRUDRepository<String, Account> {


    /**
     * Find all account from the repository.
     * @return all accounts.
     * @throws TransactionException if there is problem during getting accounts.
     */
    List<Account> findAll() throws TransactionException;

    /**
     * Find an account using their account number from the repository.
     * @param accountNumber find account by account number.
     * @return account holder for account holder id.
     * @throws TransactionException if there is a problem getting a account holder.
     */
    Account findByPK(String accountNumber) throws TransactionException;

    /**
     * Create an account in the repository.
     * @param account account to create.
     * @return number of accounts created.
     * @throws TransactionException if there is a problem getting a account holder.
     */
    Account create(Account account) throws TransactionException;

    /**
     * Delete an account with a account number from the repository.
     * @param accountNumber account number
     * @return number of account holders deleted.
     * @throws TransactionException if there is a problem getting a account holder.
     */
    void delete(String accountNumber) throws TransactionException;


    /**
     * Update account.
     * @param account the account to update.
     * @return the updated account.
     * @throws TransactionException if there is a problem getting a account holder.
     */
    Account update(Account account) throws TransactionException;

    /**
     * Transfer funds between user accounts.
     * @param userTransaction the transaction that needs to be performed.
     * @return the transfer that has happened.
     * @throws TransactionException if there is a problem getting a account holder.
     */
    TransferAccountBalanceResponse transferAccountBalance(UserTransactionVO userTransaction) throws TransactionException;

}
