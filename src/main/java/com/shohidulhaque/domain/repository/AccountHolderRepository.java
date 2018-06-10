package com.shohidulhaque.domain.repository;

import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.model.AccountHolder;

import java.util.List;

public interface AccountHolderRepository extends CRUDRepository<String, AccountHolder>{


    /**
     * Find all account holders from the repository.
     * @return all account holders.
     * @throws TransactionException if there is problem during getting account holders.
     */
    List<AccountHolder> findAll() throws TransactionException;

    /**
     * Find an account holder using their user name from the repository.
     * @param accountHolderId find account holder by account holder id.
     * @return account holder for account holder id.
     * @throws TransactionException if is a problem getting a account holder.
     */
    AccountHolder findByPK(String accountHolderId) throws TransactionException;


    /**
     * Create an account holder in the repository.
     * @param accountHolder account to create.
     * @return the newly created account.
     * @throws TransactionException if there is problem during creating an account holder.
     */
    AccountHolder create(AccountHolder accountHolder) throws TransactionException;

    /**
     * Update account holder in the repository.
     * @param accountHolder account holder to update in the repository.
     * @return the updated account.
     * @throws TransactionException if there is problem during updating an account holder.
     */
    AccountHolder update(AccountHolder accountHolder) throws TransactionException;

    /**
     * Delete a account holder with an account holder id  from the repository.
     * @param accountHolderId account holder id
     * @return number of account holders deleted.
     * @throws TransactionException if is a problem deleting an account holder.
     */
    void delete(String accountHolderId) throws TransactionException;

}
