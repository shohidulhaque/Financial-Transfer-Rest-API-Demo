package com.shohidulhaque.repository;

import com.shohidulhaque.Application;
import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.model.AccountHolder;
import com.shohidulhaque.domain.repository.RepositoryFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertNotEquals;

public class TestAccountHolderRepository {

    private static final RepositoryFactory repositoryFactory = RepositoryFactory.getRepositoryFactory();

    @BeforeClass
    public static void setup() {
        repositoryFactory.initialiseDatabase(TestAccountRepository.class.getResourceAsStream(Application.SQL_DATA_FILE_PATH));
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetAllAccountHolders() throws TransactionException {
        List<AccountHolder> allAccounts = repositoryFactory.getAccountHolderRepository().getAllAccountHolders();
        assertTrue("more then one account should have been returned.",allAccounts.size() > 1);
    }

    @Test
    public void testGetAccountHolderByAccountHolderId() throws TransactionException {
        AccountHolder accountHolder = repositoryFactory.getAccountHolderRepository().getAccountHolderByAccountHolderId("123yangluo");
        assertNotNull("account should have been returned.", accountHolder);
        assertTrue("an account with the wrong account holder id was returned.",accountHolder.getAccountHolderId().equals("123yangluo"));
    }

    @Test
    public void testGetNonExistingAccountHolder() throws TransactionException {
        AccountHolder accountHolder = repositoryFactory.getAccountHolderRepository().getAccountHolderByAccountHolderId("11223123734343");
        assertTrue("no account should have been returned with the account holder id 11223123734343",accountHolder == null);
    }

    @Test
    public void testCreateAccountHolder() throws TransactionException {
        AccountHolder accountHolder = new AccountHolder("64737647387", "Shohidul", "Haque");
        long id = repositoryFactory.getAccountHolderRepository().createAccountHolder(accountHolder);
        AccountHolder afterCreation = repositoryFactory.getAccountHolderRepository().getAccountHolderByAccountHolderId("64737647387");
        assertNotNull("the account holder has not been created", afterCreation);
        assertTrue(afterCreation.equals(accountHolder));
    }

    @Test
    public void testDeleteAccountHolder() throws TransactionException {
        int rowCount = repositoryFactory.getAccountHolderRepository().deleteAccountHolderByAccountHolderId("123liusisi");
        // assert one row(user) deleted
        assertNotEquals("account 123liusisi has not been deleted.",rowCount == 1);
        // assert user no longer there
        assertTrue("account 123liusisi still exist.",repositoryFactory.getAccountHolderRepository().getAccountHolderByAccountHolderId("87523123") == null);
    }

    @Test
    public void testDeleteNonExistingAccountHolder() throws TransactionException {
        int rowCount = repositoryFactory.getAccountHolderRepository().deleteAccountHolderByAccountHolderId("123liusisiLLO");
        assertTrue("found a account that should not exist.", rowCount == 0);

    }
}
