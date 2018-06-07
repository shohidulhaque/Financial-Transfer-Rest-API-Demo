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

import static junit.framework.TestCase.assertTrue;

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
        assertTrue(allUsers.size() > 1);
    }

    @Test
    public void testGetUserById() throws TransactionException {
        AccountHolder u = repositoryFactory.getAccountHolderRepository().getAccountHolderById(2L);
        assertTrue(u.getAccountHolderId().equals("123qinfran"));
    }

    @Test
    public void testGetNonExistingUserById() throws TransactionException {
        AccountHolder u = repositoryFactory.getAccountHolderRepository().getAccountHolderById(500L);
        assertTrue(u == null);
    }

    @Test
    public void testGetNonExistingUserByName() throws TransactionException {
        AccountHolder u = repositoryFactory.getAccountHolderRepository().getAccountHolderByAccountHolderId("abcdeftg");
        assertTrue(u == null);
    }

    @Test
    public void testCreateUser() throws TransactionException {
        AccountHolder u = new AccountHolder(28L, "liandre", "firstName", "lastName");
        long id = repositoryFactory.getAccountHolderRepository().createAccountHolder(u);
        AccountHolder uAfterInsert = repositoryFactory.getAccountHolderRepository().getAccountHolderById(id);
        assertTrue(uAfterInsert.getAccountHolderId().equals("liandre"));
    }

    @Test
    public void testUpdateUser() throws TransactionException {
        AccountHolder u = new AccountHolder(1L, "test2", "firstName", "lastName");
        int rowCount = repositoryFactory.getAccountHolderRepository().updateAccountHolder(1L, u);
        // assert one row(user) updated
        assertTrue(rowCount == 1);
        //assertTrue(repositoryFactory.getAccountHolderRepository().getAccountHolderById(1L).getEmailAddress().equals("yanglu@gmail.com"));
    }

    @Test
    public void testUpdateNonExistingUser() throws TransactionException {
        AccountHolder u = new AccountHolder(500L, "test2", "firstName", "lastName");
        int rowCount = repositoryFactory.getAccountHolderRepository().updateAccountHolder(500L, u);
        // assert one row(user) updated
        assertTrue(rowCount == 0);
    }

    @Test
    public void testDeleteUser() throws TransactionException {

        int rowCount = repositoryFactory.getAccountHolderRepository().deleteAccountHolder(1L);
        // assert one row(user) deleted
        assertTrue(rowCount == 1);
        // assert user no longer there
        assertTrue(repositoryFactory.getAccountHolderRepository().getAccountHolderById(1L) == null);
    }

    @Test
    public void testDeleteNonExistingUser() throws TransactionException {
        int rowCount = repositoryFactory.getAccountHolderRepository().deleteAccountHolder(500L);
        // assert no row(user) deleted
        assertTrue(rowCount == 0);

    }

}
