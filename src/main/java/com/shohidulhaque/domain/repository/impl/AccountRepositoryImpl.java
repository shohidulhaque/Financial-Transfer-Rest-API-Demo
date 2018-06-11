package com.shohidulhaque.domain.repository.impl;

import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.model.Account;
import com.shohidulhaque.domain.repository.AccountRepository;
import com.shohidulhaque.domain.repository.RepositoryFactory;
import com.shohidulhaque.domain.service.TransferAccountBalanceResponse;
import com.shohidulhaque.domain.valueobject.AccountTransferVO;
import com.shohidulhaque.domain.valueobject.UserTransactionVO;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountRepositoryImpl implements AccountRepository {

    private static final String SQL_GET_ACCOUNT_BY_ACCOUNT_NUMBER = "SELECT * FROM Account WHERE AccountNumber = ? ";
    private static final String SQL_LOCK_ACCOUNT_BY_ACCOUNT_NUMBER = "SELECT * FROM Account WHERE AccountNumber = ? FOR UPDATE";
    private static final String SQL_LOCK_ACCOUNT_BY_ACCOUNT_ID = "SELECT * FROM Account WHERE ID = ? FOR UPDATE";

    private static final String SQL_CREATE_ACCOUNT = "INSERT INTO Account (AccountNumber, AccountHolder, SortCode, Balance) VALUES (?, ?, ?, ?)";

    private static final String SQL_UPDATE_ACCOUNT_BALANCE_BY_ACCOUNT_NUMBER = "UPDATE Account SET Balance = ? WHERE AccountNumber = ? ";
    private final static String SQL_UPDATE_ACCOUNT = "UPDATE Account SET  AccountNumber = ?, SortCode = ?, Balance = ? WHERE Id = ?";

    private static final String SQL_GET_ALL_ACCOUNT = "SELECT * FROM Account";
    private static final String SQL_DELETE_ACCOUNT_BY_ACCOUNT_NUMBER = "DELETE FROM Account WHERE AccountNumber = ?";
    private static final String SQL_CREATE_TRANSACTION = "INSERT INTO AccountTransfer(Amount, FromAccountId, ToAccountId, TransactionTime) VALUES (?,?,?,?)";

    private static final BigDecimal ZERO = new BigDecimal(0).setScale(2, RoundingMode.HALF_EVEN);

    private static final Logger LOGGER = Logger.getLogger(AccountRepositoryImpl.class);

    private static final String ID = "Id";
    private static final String ACCOUNT_HOLDER = "AccountHolder";
    private static final String ACCOUNT_NUMBER = "AccountNumber";
    private static final String SORT_CODE = "SortCode";
    private static final String BALANCE = "BALANCE";

    /**
     * Get all accounts.
     */
    public List<Account> findAll() throws TransactionException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Account> allAccounts = new ArrayList<>();
        try {
            conn = RepositoryFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ALL_ACCOUNT);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Account acc = new Account(
                        rs.getLong(ID),
                        rs.getLong(ACCOUNT_HOLDER),
                        rs.getString(ACCOUNT_NUMBER),
                        rs.getString(SORT_CODE),
                        rs.getBigDecimal(BALANCE));
                allAccounts.add(acc);
            }
            return allAccounts;
        } catch (SQLException e) {
            throw new TransactionException("error when reading account data.", TransactionException.ResponseCode.FAILURE.name(), e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    /**
     * Get account by account number.
     */
    public Account findByPK(String accountNumber) throws TransactionException {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        Account account = null;
        try {
            conn = RepositoryFactory.getConnection();
            statement = conn.prepareStatement(SQL_GET_ACCOUNT_BY_ACCOUNT_NUMBER);
            statement.setString(1, accountNumber);
            rs = statement.executeQuery();
            if (rs.next()) {
                account = new Account(
                        rs.getLong(ID),
                        rs.getLong(ACCOUNT_HOLDER),
                        rs.getString(ACCOUNT_NUMBER),
                        rs.getString(SORT_CODE),
                        rs.getBigDecimal(BALANCE));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("accessed account with " + account);
            }
            return account;
        } catch (SQLException e) {
            throw new TransactionException("error reading account data.", TransactionException.ResponseCode.FAILURE.name(), e);
        } finally {
            DbUtils.closeQuietly(conn, statement, rs);
        }
    }


    /**
     * Create account.
     */
    public Account create(Account account) throws TransactionException {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            conn = RepositoryFactory.getConnection();
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(SQL_CREATE_ACCOUNT);
            statement.setString(1, account.getAccountNumber());
            statement.setLong(2, account.getAccountHolderId());
            statement.setString(3, account.getSortCode());
            statement.setBigDecimal(4, account.getBalance());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                LOGGER.error("unable to create account.");
                throw new TransactionException("account could not be created.", TransactionException.ResponseCode.FAILURE.name());
            }
            generatedKeys = statement.getGeneratedKeys();
            conn.commit();
            if (generatedKeys.next()) {
                account.setId(generatedKeys.getLong(1));
                return account;
            } else {
                LOGGER.error("unable to create an account, unable to obtain ID.");
                throw new TransactionException("account could not be created.", TransactionException.ResponseCode.FAILURE.name());
            }
        } catch (SQLException e) {
            LOGGER.error("error creating account for " + account);
            // rollback transaction if exception occurs
            LOGGER.error("rollback initiated for " + account.getAccountNumber(), e);

            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                throw new TransactionException("failed to rollback transaction for account " + account.getAccountNumber(), TransactionException.ResponseCode.FAILURE.name(), re);
            }

            throw new TransactionException("unable to create account " + account, TransactionException.ResponseCode.FAILURE.name(), e);
        } finally {
            DbUtils.closeQuietly(conn, statement, generatedKeys);
        }
    }

    /**
     * Delete account by account number.
     */
    public void delete(String accountNumber) throws TransactionException {
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = RepositoryFactory.getConnection();
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(SQL_DELETE_ACCOUNT_BY_ACCOUNT_NUMBER);
            statement.setString(1, accountNumber);
            statement.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            // rollback transaction if exception occurs
            LOGGER.error("rollback initiated for " + accountNumber, e);
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                throw new TransactionException("failed to rollback transaction for account " + accountNumber, TransactionException.ResponseCode.FAILURE.name(), re);
            }

            throw new TransactionException("unable to delete account for" + accountNumber, TransactionException.ResponseCode.FAILURE.name(), e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(statement);
        }
    }

    /**
     * Update account balance.
     */
    public Account update(Account account) throws TransactionException {
        Connection conn = null;
        PreparedStatement lockStatement = null;
        PreparedStatement updateStatement = null;
        ResultSet rs = null;
        Account targetAccount = null;
        try {
            conn = RepositoryFactory.getConnection();
            conn.setAutoCommit(false);
            // lock account for writing:
            lockStatement = conn.prepareStatement(SQL_LOCK_ACCOUNT_BY_ACCOUNT_ID);
            lockStatement.setLong(1, account.getId());
            rs = lockStatement.executeQuery();
            if (rs.next()) {
                targetAccount = new Account(

                        rs.getLong(ID),
                        rs.getLong(ACCOUNT_HOLDER),
                        rs.getString(ACCOUNT_NUMBER),
                        rs.getString(SORT_CODE),
                        rs.getBigDecimal(BALANCE));

                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("locked account" + targetAccount);
            }

            if (targetAccount == null) {
                throw new TransactionException("failed to lock account " + account.getAccountNumber(), TransactionException.ResponseCode.FAILURE.name());
            }
            // update account upon success locking
            if (account.getBalance().compareTo(ZERO) < 0) {
                throw new TransactionException("not sufficient fonds for " + account.getAccountNumber(), TransactionException.ResponseCode.FAILURE.name());
            }

            targetAccount.setBalance(account.getBalance());
            targetAccount.setAccountNumber(account.getAccountNumber());
            targetAccount.setSortCode(account.getSortCode());

            updateStatement = conn.prepareStatement(SQL_UPDATE_ACCOUNT);

            updateStatement.setString(1, targetAccount.getAccountNumber());
            updateStatement.setString(2, targetAccount.getSortCode());
            updateStatement.setBigDecimal(3, targetAccount.getBalance());
            updateStatement.setLong(4, targetAccount.getId());

            updateStatement.executeUpdate();
            conn.commit();

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("new balanace after update" + targetAccount);
        } catch (SQLException se) {
            // rollback transaction if exception occurs
            LOGGER.error("rollback initiated for " + account.getAccountNumber(), se);
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                throw new TransactionException("failed to rollback transaction for account " + account.getAccountNumber(), TransactionException.ResponseCode.FAILURE.name(), re);
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStatement);
            DbUtils.closeQuietly(updateStatement);
        }
        return targetAccount;
    }

    /**
     * Transfer balance between two accounts.
     */
    public TransferAccountBalanceResponse transferAccountBalance(UserTransactionVO userTransaction) throws TransactionException {
        int result = -1;
        Connection conn = null;
        PreparedStatement lockStatment = null;
        PreparedStatement updateStatement = null;
        PreparedStatement createTransactonStatment = null;

        ResultSet rs = null;

        Account fromAccount = null;
        Account toAccount = null;

        AccountTransferVO accountTransferVO = new AccountTransferVO(
                userTransaction.getAmount(),
                userTransaction.getFromAccountNumber(),
                userTransaction.getToAccountNumber(), new Date());

        try {
            conn = RepositoryFactory.getConnection();
            conn.setAutoCommit(false);
            lockStatment = conn.prepareStatement(SQL_LOCK_ACCOUNT_BY_ACCOUNT_NUMBER);
            lockStatment.setString(1, accountTransferVO.getFromAccountNumber());
            rs = lockStatment.executeQuery();
            if (rs.next()) {
                fromAccount = new Account(
                        rs.getLong(ID),
                        rs.getLong(ACCOUNT_HOLDER),
                        rs.getString(ACCOUNT_NUMBER),
                        rs.getString(SORT_CODE),
                        rs.getBigDecimal(BALANCE));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("locked account " + fromAccount);
            }
            lockStatment = conn.prepareStatement(SQL_LOCK_ACCOUNT_BY_ACCOUNT_NUMBER);
            lockStatment.setString(1, userTransaction.getToAccountNumber());
            rs = lockStatment.executeQuery();
            if (rs.next()) {
                toAccount = new Account(
                        rs.getLong(ID),
                        rs.getLong(ACCOUNT_HOLDER),
                        rs.getString(ACCOUNT_NUMBER),
                        rs.getString(SORT_CODE),
                        rs.getBigDecimal(BALANCE));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("locked account " + toAccount);
            }

            // check locking status
            if (fromAccount == null || toAccount == null) {
                throw new TransactionException("unable to lock accounts " +
                        accountTransferVO.getFromAccountNumber() + " and " +
                        accountTransferVO.getToAccountNumber(), TransactionException.ResponseCode.FAILURE.name());
            }

            // check there is enough fund in source account
            BigDecimal delta = fromAccount.getBalance().subtract(userTransaction.getAmount());
            if (delta.compareTo(new BigDecimal(0).setScale(2, RoundingMode.HALF_EVEN)) < 0) {
                throw new TransactionException("insufficient funds from source.", TransactionException.ResponseCode.FAILURE.name(), accountTransferVO.getFromAccountNumber(), accountTransferVO.getToAccountNumber(), accountTransferVO.getAmount());
            }

            updateStatement = conn.prepareStatement(SQL_UPDATE_ACCOUNT_BALANCE_BY_ACCOUNT_NUMBER);
            updateStatement.setBigDecimal(1, delta);
            updateStatement.setString(2, userTransaction.getFromAccountNumber());
            updateStatement.addBatch();
            updateStatement.setBigDecimal(1, toAccount.getBalance().add(userTransaction.getAmount()));
            updateStatement.setString(2, userTransaction.getToAccountNumber());
            updateStatement.addBatch();
            int[] rowsUpdated = updateStatement.executeBatch();
            if (rowsUpdated.length > 1)
                result = rowsUpdated[0] + rowsUpdated[1];
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("number of rows updated for the transfer : " + result);
            }

            //update the transaction table.
            createTransactonStatment = conn.prepareStatement(SQL_CREATE_TRANSACTION);
            createTransactonStatment.setBigDecimal(1, accountTransferVO.getAmount());
            createTransactonStatment.setLong(2, fromAccount.getId());
            createTransactonStatment.setLong(3, toAccount.getId());
            createTransactonStatment.setDate(4, new java.sql.Date(accountTransferVO.getTransactionTime().getTime()));
            rowsUpdated = createTransactonStatment.executeBatch();
            if (rowsUpdated.length > 0)
                result = rowsUpdated[0];
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("a new transaction record has been inserted : " + accountTransferVO);
            }
            conn.commit();
        } catch (SQLException se) {
            // rollback transaction if exception occurs
            LOGGER.error("rollback initiated for " + userTransaction, se);
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                throw new TransactionException("failed to rollback transaction for " + userTransaction, TransactionException.ResponseCode.FAILURE.name(), re);
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStatment);
            DbUtils.closeQuietly(updateStatement);
            DbUtils.closeQuietly(createTransactonStatment);
        }
        //throw exception
        return new TransferAccountBalanceResponse(accountTransferVO, result);
    }

}
