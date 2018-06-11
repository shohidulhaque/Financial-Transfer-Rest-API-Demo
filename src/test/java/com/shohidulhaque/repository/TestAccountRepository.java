package com.shohidulhaque.repository;

import com.shohidulhaque.Application;
import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.model.Account;
import com.shohidulhaque.domain.repository.RepositoryFactory;
import com.shohidulhaque.domain.valueobject.UserTransactionVO;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class TestAccountRepository {

    private static final RepositoryFactory repositoryFactory = RepositoryFactory.getRepositoryFactory();

    @BeforeClass
    public static void setup() {
        repositoryFactory.initialiseDatabase(TestAccountRepository.class.getResourceAsStream(Application.SQL_DATA_FILE_PATH));
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testCreateAccount() throws TransactionException {
        BigDecimal balance = new BigDecimal(10).setScale(2, RoundingMode.HALF_EVEN);
        Account a = new Account(1L, "01122312373434309", "434567", balance);
        repositoryFactory.getAccountRepository().create(a);
        assertNotEquals ("the wrong account has been returned.", a.getId(), -1);
    }

    @Test
    public void testGetAllAccounts() throws TransactionException {
        List<Account> allAccounts = repositoryFactory.getAccountRepository().findAll();
        assertTrue("there should be more than one account.",allAccounts.size() > 1);
    }

    @Test
    public void testGetAccountByAccountNumber() throws TransactionException {
        Account account = repositoryFactory.getAccountRepository().findByPK("21223123");
        assertNotNull("cannot find account 21223123", account);
        assertEquals("cannot find account with account number 21223123", "21223123", account.getAccountNumber());
    }

    @Test
    public void testGetNonExistingAccount() throws TransactionException {
        Account account = repositoryFactory.getAccountRepository().findByPK("11223123734343");
        assertTrue("account should not exist.",account == null);
    }

    @Test
    public void testDeleteAccount() throws TransactionException {
        repositoryFactory.getAccountRepository().delete("31223123");
        assertTrue("the account 31223123 has not been deleted.",repositoryFactory.getAccountRepository().findByPK("31223123") == null);
    }

    @Test
    public void testDeleteNonExistingAccount() throws TransactionException {
        repositoryFactory.getAccountRepository().delete("1122312373434444309");
        Account acc = repositoryFactory.getAccountRepository().findByPK("1122312373434444309");
        // assert no row(user) deleted
        assertTrue("account 1122312373434444309 should not exist.",acc == null);

    }

    @Test(expected = TransactionException.class)
    public void testTransactionNotEnoughFund() throws TransactionException {
        BigDecimal amount = new BigDecimal(10000000).setScale(2, RoundingMode.HALF_EVEN);
        UserTransactionVO userTransaction = new UserTransactionVO(amount, "31223123", "21223123");
        repositoryFactory.getAccountRepository().transferAccountBalance(userTransaction);
    }

    @Test
    public void testUpdateAccount() throws TransactionException{
        Account acc = repositoryFactory.getAccountRepository().findByPK("87523123");
        acc.setSortCode("236798");
        acc.setAccountNumber("875231235");
        acc.setBalance(new BigDecimal(10000000).setScale(2, RoundingMode.HALF_EVEN));
        Account up = repositoryFactory.getAccountRepository().update(acc);
        assertNotNull("account object is null.", up);
        Account acc2 = repositoryFactory.getAccountRepository().findByPK("875231235");
        assertEquals("account objects are different.", acc, acc2);
    }
}