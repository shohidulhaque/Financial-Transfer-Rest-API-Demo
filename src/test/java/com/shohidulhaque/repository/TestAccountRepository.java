package com.shohidulhaque.repository;

import com.shohidulhaque.Application;
import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.model.Account;
import com.shohidulhaque.domain.repository.RepositoryFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

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
    public void testGetAllAccounts() throws TransactionException {
        List<Account> allAccounts = repositoryFactory.getAccountRepository().getAllAccounts();
        assertTrue(allAccounts.size() > 1);
    }

    @Test
    public void testGetAccountByAccountNumber() throws TransactionException {
        Account account = repositoryFactory.getAccountRepository().getAccount("31223123");
        assertTrue(account.getAccountNumber().equals("31223123"));
    }

    @Test
    public void testGetNonExistingAccount() throws TransactionException {
        Account account = repositoryFactory.getAccountRepository().getAccount("11223123734343");
        assertTrue(account == null);
    }

    @Test
    public void testCreateAccount() throws TransactionException {
        BigDecimal balance = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        Account a = new Account(1L, "01122312373434309", "434567", balance);
        long id = repositoryFactory.getAccountRepository().createAccount(a);
        Account afterCreation = repositoryFactory.getAccountRepository().getAccountById(id);
        //assertTrue(afterCreation.getUserName().equals("test2"));
        assertTrue(afterCreation.getBalance().equals(balance));
    }

    @Test
    public void testDeleteAccount() throws TransactionException {
        int rowCount = repositoryFactory.getAccountRepository().deleteAccount("87523123");
        // assert one row(user) deleted
        assertTrue(rowCount == 1);
        // assert user no longer there
        assertTrue(repositoryFactory.getAccountRepository().getAccount("87523123") == null);
    }

    @Test
    public void testDeleteNonExistingAccount() throws TransactionException {
        int rowCount = repositoryFactory.getAccountRepository().deleteAccount("1122312373434444309");
        // assert no row(user) deleted
        assertTrue(rowCount == 0);

    }

    @Test
    public void testUpdateAccountBalanceWithSufficientFund() throws TransactionException {
        BigDecimal deltaDeposit = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal afterDeposit = new BigDecimal(150).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdated = repositoryFactory.getAccountRepository().updateAccountBalance("31223123", deltaDeposit);
        assertTrue(rowsUpdated == 1);
        assertTrue(repositoryFactory.getAccountRepository().getAccount("31223123").getBalance().equals(afterDeposit));
        BigDecimal deltaWithDraw = new BigDecimal(-50).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal afterWithDraw = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdatedW = repositoryFactory.getAccountRepository().updateAccountBalance("31223123", deltaWithDraw);
        assertTrue(rowsUpdatedW == 1);
        assertTrue(repositoryFactory.getAccountRepository().getAccount("31223123").getBalance().equals(afterWithDraw));

    }

    @Test(expected = TransactionException.class)
    public void testUpdateAccountBalanceWithNotEnoughFund() throws TransactionException {
        BigDecimal deltaWithDraw = new BigDecimal(-50000).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdatedW = repositoryFactory.getAccountRepository().updateAccountBalance("31223123", deltaWithDraw);
        assertTrue(rowsUpdatedW == 0);

    }

}