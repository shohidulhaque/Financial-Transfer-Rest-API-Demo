package com.shohidulhaque;

import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.model.Account;
import com.shohidulhaque.domain.repository.AccountRepository;
import com.shohidulhaque.domain.repository.RepositoryFactory;
import com.shohidulhaque.domain.service.TransferAccountBalanceResponse;
import com.shohidulhaque.domain.valueobject.UserTransactionVO;
import com.shohidulhaque.repository.TestAccountRepository;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertTrue;

public class ThreadTest {

    private static RepositoryFactory repositoryFactory = RepositoryFactory.getRepositoryFactory();
    private static Logger logger = Logger.getLogger(ThreadTest.class);
    private static final int THREADS_SIZE = 50;

    @BeforeClass
    public static void setup() {
        repositoryFactory.initialiseDatabase(TestAccountRepository.class.getResourceAsStream(Application.SQL_DATA_FILE_PATH));
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testSingleThreadTransfer() throws TransactionException {

        final AccountRepository accountRepository = repositoryFactory.getAccountRepository();

        BigDecimal transferAmount = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);

        UserTransactionVO transaction = new UserTransactionVO(transferAmount, "11111111", "10101010");

        long startTime = System.currentTimeMillis();
        TransferAccountBalanceResponse transferAccountBalanceResponse = accountRepository.transferAccountBalance(transaction);
        long endTime = System.currentTimeMillis();

        logger.info("time taken to transfer " + (endTime - startTime) + "ms");

        Account accountFrom = accountRepository.findByPK("11111111");
        Account accountTo = accountRepository.findByPK("10101010");

        assertTrue("from account has not been deducted from correctly ", accountFrom.getBalance().compareTo(new BigDecimal(450.0000).setScale(4, RoundingMode.HALF_EVEN)) == 0);
        assertTrue("to account has not been deposited to correctly ",accountTo.getBalance().equals(new BigDecimal(550.0000).setScale(4, RoundingMode.HALF_EVEN)));

    }

    @Test
    public void testMultiThreadedTransfer() throws InterruptedException, TransactionException {
        AccountRepository accountRepository = repositoryFactory.getAccountRepository();
        // transfer a total of 200USD from 100USD balance in multi-threaded
        // mode, expect half of the transaction fail
        final CountDownLatch latch = new CountDownLatch(THREADS_SIZE);
        for (int i = 0; i < THREADS_SIZE; i++) {
            new Thread(() -> {
                                try {
                                        UserTransactionVO transaction = new UserTransactionVO(
                                                                                new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN),
                                                                                "50505050",
                                                                                "90909090"
                                                                                );
                        accountRepository.transferAccountBalance(transaction);
                    } catch (Exception e) {
                        logger.error("error occurred during transfer ", e);
                    } finally {
                        latch.countDown();
                    }
                }
            ).start();
        }

        latch.await();

        Account accountFrom = accountRepository.findByPK("50505050");

        Account accountTo = accountRepository.findByPK("90909090");

        logger.debug("Account From: " + accountFrom);

        logger.debug("Account From: " + accountTo);

        assertTrue(accountFrom.getBalance().equals(new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN)));
        assertTrue(accountTo.getBalance().equals(new BigDecimal(500).setScale(4, RoundingMode.HALF_EVEN)));

    }

    @Test
    public void testTransferFailOnDataBaseLock() throws TransactionException, SQLException {
        final String SQL_LOCK_ACC = "SELECT * FROM Account WHERE AccountNumber = '20202020' FOR UPDATE";
        Connection conn = null;
        PreparedStatement lockStmt = null;
        ResultSet rs = null;
        Account fromAccount = null;

        try {
            conn = repositoryFactory.getConnection();
            conn.setAutoCommit(false);
            // lock account for writing:
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC);
            rs = lockStmt.executeQuery();

            if (rs.next()) {
                        fromAccount = new Account(
                                rs.getLong("Id"),
                                rs.getLong("AccountHolder"),
                                rs.getString("AccountNumber"),
                                rs.getString("SortCode"),
                                rs.getBigDecimal("Balance"));

                if (logger.isDebugEnabled())
                    logger.debug("accounted locked " + fromAccount);
            }

            if (fromAccount == null) {
                throw new TransactionException("locking error during test", TransactionException.ResponseCode.SUCCESS.name());
            }
            // after lock account 5, try to transfer from account 6 to 5
            // default h2 timeout for acquire lock is 1sec
            BigDecimal transferAmount = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
            UserTransactionVO transaction = new UserTransactionVO(transferAmount, "20202020", "33333333");
            repositoryFactory.getAccountRepository().transferAccountBalance(transaction);
            conn.commit();
        } catch (Exception e) {
            logger.error("Exception occurred, initiate a rollback");
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                logger.error("Fail to rollback transaction", re);
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStmt);
        }

        // now inspect account 3 and 4 to verify no transaction occurred
        BigDecimal originalBalance = new BigDecimal(500).setScale(4, RoundingMode.HALF_EVEN);
        assertTrue("funds have been transferred.",repositoryFactory.getAccountRepository().findByPK("20202020").getBalance().equals(originalBalance));

    }
}
