package com.shohidulhaque.domain.repository;

import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.model.Account;
import com.shohidulhaque.domain.service.TransferAccountBalanceResponse;
import com.shohidulhaque.domain.valueobject.UserTransactionVO;

import java.math.BigDecimal;
import java.util.List;

public interface AccountRepository {

    List<Account> getAllAccounts() throws TransactionException;
    Account getAccount(String accountNumber) throws TransactionException;
    Account  getAccountById(long accountId) throws TransactionException;
    long createAccount(Account account) throws TransactionException;
    int deleteAccount(String accountNumber) throws TransactionException;
    int updateAccountBalance(String accountNumber, BigDecimal deltaAmount) throws TransactionException;
    TransferAccountBalanceResponse transferAccountBalance(UserTransactionVO userTransaction) throws TransactionException;
}
