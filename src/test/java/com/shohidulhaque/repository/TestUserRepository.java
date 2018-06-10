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
        log.debug("setting up test database and sample data.");
        repositoryFactory.initialiseDatabase(TestAccountRepository.class.getResourceAsStream(Application.SQL_DATA_FILE_PATH));
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetAllUsers() throws TransactionException {
        List<AccountHolder> allUsers = repositoryFactory.getAccountHolderRepository().findAll();
        assertTrue("there should more then one account holder.", allUsers.size() > 1);
    }

    @Test
    public void testGetNonExistingAccountHolderByAccountHolder() throws TransactionException {
        AccountHolder u = repositoryFactory.getAccountHolderRepository().findByPK("abcdeftg");
        assertTrue("account holder should not exist.", u == null);
    }

    @Test
    public void testCreateAccountHolder() throws TransactionException {
        AccountHolder u = new AccountHolder("liandre", "firstName", "lastName");
        repositoryFactory.getAccountHolderRepository().create(u);
        AccountHolder uAfterInsert = repositoryFactory.getAccountHolderRepository().findByPK("liandre");
        assertTrue("account holder has not been created.",uAfterInsert.equals(u));
    }

    @Test
    public void testUpdateAccountHolder() throws TransactionException {
        AccountHolder u = repositoryFactory.getAccountHolderRepository().findByPK("5644565466");

        u.setFirstName("TEST FIRST");
        u.setLastName("TEST SECOND");
        u.setAccountHolderId("0000000000");

        AccountHolder accountHolderUpdate = repositoryFactory.getAccountHolderRepository().update(u);
        assertTrue("account holder has not been updated.", u.equals(accountHolderUpdate));
        AccountHolder acc = repositoryFactory.getAccountHolderRepository().findByPK("0000000000");
        assertTrue("account holder has not been updated.", u.equals(acc));
    }

    @Test
    public void testDeleteAccountHolder() throws TransactionException {
        repositoryFactory.getAccountHolderRepository().delete("4355466546");
        assertTrue("deleted account holder exists.",repositoryFactory.getAccountHolderRepository().findByPK("4355466546") == null);
    }

    @Test
    public void testDeleteNonExistingAccountHolder() throws TransactionException {
        repositoryFactory.getAccountHolderRepository().findByPK("500L");
        assertTrue("a non existent account holder has been deleted.",repositoryFactory.getAccountHolderRepository().findByPK("500L") == null);

    }

}
