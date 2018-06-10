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
        List<AccountHolder> allAccounts = repositoryFactory.getAccountHolderRepository().findAll();
        assertTrue("more then one account should have been returned.",allAccounts.size() > 1);
    }

    @Test
    public void testGetAccountHolderByAccountHolderId() throws TransactionException {
        AccountHolder accountHolder = repositoryFactory.getAccountHolderRepository().findByPK("123yangluo");
        assertNotNull("account should have been returned.", accountHolder);
        assertTrue("an account with the wrong account holder id was returned.",accountHolder.getAccountHolderId().equals("123yangluo"));
    }

    @Test
    public void testGetNonExistingAccountHolder() throws TransactionException {
        AccountHolder accountHolder = repositoryFactory.getAccountHolderRepository().findByPK("11223123734343");
        assertTrue("no account should have been returned with the account holder id 11223123734343",accountHolder == null);
    }

    @Test
    public void testCreateAccountHolder() throws TransactionException {
        AccountHolder accountHolder = new AccountHolder("64737647387", "Shohidul", "Haque");
        repositoryFactory.getAccountHolderRepository().create(accountHolder);
        assertNotEquals("the account holder has not been created", accountHolder.getId(), -1);
    }

    @Test
    public void testDeleteAccountHolder() throws TransactionException {
        repositoryFactory.getAccountHolderRepository().delete("87523123");
        assertTrue("account 87523123 still exist.",repositoryFactory.getAccountHolderRepository().findByPK("87523123") == null);
    }

    @Test
    public void testDeleteNonExistingAccountHolder() throws TransactionException {
        repositoryFactory.getAccountHolderRepository().delete("123liusisiLLO");
        AccountHolder acc = repositoryFactory.getAccountHolderRepository().findByPK("123liusisiLLO");
        assertNull("found a account that should not exist.", acc);

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



}
