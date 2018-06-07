package com.shohidulhaque.repository;

import com.shohidulhaque.Application;
import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.model.AccountHolder;
import com.shohidulhaque.domain.repository.RepositoryFactory;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestUserRepository {

    private static final RepositoryFactory repositoryFactory = RepositoryFactory.getRepositoryFactory();
    private static Logger log = Logger.getLogger(TestUserRepository.class);

    @BeforeClass
    public static void setup() {
        // prepare test database and test data by executing sql script demo.sql
        log.debug("setting up test database and sample data....");
        repositoryFactory.initialiseDatabase(TestAccountRepository.class.getResourceAsStream(Application.SQL_DATA_FILE_PATH));
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetAllUsers() throws TransactionException {
        List<AccountHolder> allUsers = repositoryFactory.getAccountHolderRepository().getAllAccountHolders();
        assertTrue("there should more then one account holder.", allUsers.size() > 1);
    }

    @Test
    public void testGetAccountHolderById() throws TransactionException {
        AccountHolder u = repositoryFactory.getAccountHolderRepository().getAccountHolderById(2L);
        assertNotNull("account holder cannot be found for id 2", u);
        assertEquals("account holder cannot be found.", 2L, u.getId());
    }

    @Test
    public void testGetNonExistingAccountHolderById() throws TransactionException {
        AccountHolder u = repositoryFactory.getAccountHolderRepository().getAccountHolderById(500L);
        assertTrue("account holder should not exist.",u == null);
    }

    @Test
    public void testGetNonExistingAccountHolderByAccountHolderId() throws TransactionException {
        AccountHolder u = repositoryFactory.getAccountHolderRepository().getAccountHolderByAccountHolderId("abcdeftg");
        assertTrue("account holder should not exist.", u == null);
    }

    @Test
    public void testCreateAccountHolderId() throws TransactionException {
        AccountHolder u = new AccountHolder(28L, "liandre", "firstName", "lastName");
        long id = repositoryFactory.getAccountHolderRepository().createAccountHolder(u);
        AccountHolder uAfterInsert = repositoryFactory.getAccountHolderRepository().getAccountHolderById(id);
        assertTrue("account holder has not been created.",uAfterInsert.getAccountHolderId().equals("liandre"));
    }

    @Test
    public void testUpdateAccountHolderId() throws TransactionException {
        AccountHolder u = new AccountHolder(1L, "test2", "firstName", "lastName");
        int rowCount = repositoryFactory.getAccountHolderRepository().updateAccountHolder(1L, u);
        assertFalse("only one account holder should have been updated.", rowCount == 1);
    }

    @Test
    public void testUpdateNonAccountHolderId() throws TransactionException {
        AccountHolder u = new AccountHolder(500L, "test2", "firstName", "lastName");
        int rowCount = repositoryFactory.getAccountHolderRepository().updateAccountHolder(500L, u);
        assertTrue("one or more account holder has been updated.",rowCount == 0);
    }

    @Test
    public void testDeleteAccountHolderId() throws TransactionException {

        int rowCount = repositoryFactory.getAccountHolderRepository().deleteAccountHolder(1L);
        assertTrue("only one account holder should have been deleted.",rowCount == 1);
        // assert user no longer there
        assertTrue("deleted account holder exists.",repositoryFactory.getAccountHolderRepository().getAccountHolderById(1L) == null);

    }

    @Test
    public void testDeleteNonExistingAccountHolderId() throws TransactionException {
        int rowCount = repositoryFactory.getAccountHolderRepository().deleteAccountHolder(500L);
        // assert no row(user) deleted
        assertTrue("a non existent account holder has been deleted.",rowCount == 0);

    }

}
