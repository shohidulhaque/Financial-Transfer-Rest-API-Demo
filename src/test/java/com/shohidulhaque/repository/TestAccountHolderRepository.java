package com.shohidulhaque.repository;

import com.shohidulhaque.Application;
import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.model.AccountHolder;
import com.shohidulhaque.domain.repository.RepositoryFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

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
        assertTrue(allAccounts.size() > 1);
    }

    @Test
    public void testGetAccountHolderByAccountHolderId() throws TransactionException {
        AccountHolder accountHolder = repositoryFactory.getAccountHolderRepository().getAccountHolderByAccountHolderId("123yangluo");
        assertTrue(accountHolder.getAccountHolderId().equals("123yangluo"));
    }

    @Test
    public void testGetNonExistingAccountHolder() throws TransactionException {
        AccountHolder accountHolder = repositoryFactory.getAccountHolderRepository().getAccountHolderByAccountHolderId("11223123734343");
        assertTrue(accountHolder == null);
    }

    @Test
    public void testCreateAccountHolder() throws TransactionException {
        AccountHolder accountHolder = new AccountHolder( "64737647387", "Shohidul", "Haque");
        long id = repositoryFactory.getAccountHolderRepository().createAccountHolder(accountHolder);
        AccountHolder afterCreation = repositoryFactory.getAccountHolderRepository().getAccountHolderByAccountHolderId("64737647387");
        assertTrue(afterCreation.equals(accountHolder));
    }

    @Test
    public void testDeleteAccountHolder() throws TransactionException {
        int rowCount = repositoryFactory.getAccountHolderRepository().deleteAccountHolderByAccountHolderId("123liusisi");
        // assert one row(user) deleted
        assertTrue(rowCount == 1);
        // assert user no longer there
        assertTrue(repositoryFactory.getAccountHolderRepository().getAccountHolderByAccountHolderId("87523123") == null);
    }

    @Test
    public void testDeleteNonExistingAccountHolder() throws TransactionException {
        int rowCount = repositoryFactory.getAccountHolderRepository().deleteAccountHolderByAccountHolderId("123liusisiLLO");
        assertTrue(rowCount == 0);

    }
}
