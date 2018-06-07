package com.shohidulhaque.domain.repository;

import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.model.AccountHolder;

import java.util.List;

public interface AccountHolderRepository {
	
	List<AccountHolder> getAllAccountHolders() throws TransactionException;

	AccountHolder getAccountHolderById(long userId) throws TransactionException;

	AccountHolder getAccountHolderByAccountHolderId(String userName) throws TransactionException;

	long createAccountHolder(AccountHolder accountHolder) throws TransactionException;

	int updateAccountHolder(Long accountHolderId, AccountHolder accountHolder) throws TransactionException;

	int deleteAccountHolderByAccountHolderId(String accountHolderId) throws TransactionException;

	int deleteAccountHolder(long accounId) throws TransactionException;

}
