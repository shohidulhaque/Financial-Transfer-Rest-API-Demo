package com.shohidulhaque.domain.repository.impl;

import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.model.Account;
import com.shohidulhaque.domain.model.AccountHolder;
import com.shohidulhaque.domain.repository.AccountHolderRepository;
import com.shohidulhaque.domain.repository.RepositoryFactory;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AccountHolderRepositoryImpl implements AccountHolderRepository {

    private static final String SQL_LOCK_ACCOUNT_BY_ACCOUNT_ID = "SELECT * FROM AccountHolder WHERE ID = ? FOR UPDATE";
    private static final String SQL_GET_ACCOUNT_HOLDER_BY_ACCOUNT_HOLDER_ID = "SELECT * FROM AccountHolder WHERE AccountHolderId = ? ";
    private static final String SQL_DELETE_ACCOUNT_HOLDER_BY_ACCOUNT_HOLDER_ID = "DELETE FROM AccountHolder WHERE AccountHolderId = ? ";
    private static final String SQL_GET_ALL_ACCOUNT_HOLDER = "SELECT * FROM AccountHolder";
    private static final String SQL_CREATE_ACCOUNT_HOLDER = "INSERT INTO AccountHolder (AccountHolderId, FirstName, LastName) VALUES (?,?,?)";
    private static final String SQL_UPDATE_ACCOUNT_HOLDER = "UPDATE AccountHolder SET FirstName = ?, LastName = ?, AccountHolderId = ? WHERE Id = ? ";

    private static final Logger log = Logger.getLogger(AccountHolderRepositoryImpl.class);

    private static final String Id = "Id";
    private static final String AccountHolderId = "AccountHolderId";
    private static final String FirstName = "FirstName";
    private static final String LastName = "LastName";

    @Override
    public List<AccountHolder> findAll() throws TransactionException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<AccountHolder> users = new ArrayList<>();
        try {
            conn = RepositoryFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ALL_ACCOUNT_HOLDER);
            rs = stmt.executeQuery();
            while (rs.next()) {
                AccountHolder u = new AccountHolder(
                        rs.getLong(Id),
                        rs.getString(AccountHolderId),
                        rs.getString(FirstName),
                        rs.getString(LastName));
                users.add(u);
                if (log.isDebugEnabled())
                    log.debug("retrieved account holder " + u);
            }
            return users;
        } catch (SQLException e) {
            throw new TransactionException("error getting users", TransactionException.ResponseCode.FAILURE.name(), e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    @Override
    public AccountHolder findByPK(String accountHolderId) throws TransactionException {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        AccountHolder ac = null;
        try {
            conn = RepositoryFactory.getConnection();
            statement = conn.prepareStatement(SQL_GET_ACCOUNT_HOLDER_BY_ACCOUNT_HOLDER_ID);
            statement.setString(1, accountHolderId);
            rs = statement.executeQuery();
            if (rs.next()) {
                ac = new AccountHolder(
                        rs.getLong(Id),
                        rs.getString(AccountHolderId),
                        rs.getString(FirstName),
                        rs.getString(LastName));

                if (log.isDebugEnabled())
                    log.debug("Retrieve User: " + ac);
            }
            return ac;
        } catch (SQLException e) {
            throw new TransactionException("error reading user data", TransactionException.ResponseCode.FAILURE.name(), e);
        } finally {
            DbUtils.closeQuietly(conn, statement, rs);
        }
    }

    @Override
    public AccountHolder create(AccountHolder accountHolder) throws TransactionException {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            conn = RepositoryFactory.getConnection();
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(SQL_CREATE_ACCOUNT_HOLDER, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, accountHolder.getAccountHolderId());
            statement.setString(2, accountHolder.getFirstName());
            statement.setString(3, accountHolder.getLastName());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                log.error(" failed creating user " + accountHolder);
                throw new TransactionException("error when creating user " + accountHolder, TransactionException.ResponseCode.FAILURE.name());
            }
            generatedKeys = statement.getGeneratedKeys();
            conn.commit();
            if (generatedKeys.next()) {
                accountHolder.setId(generatedKeys.getLong(1));
                return accountHolder;
            } else {
                log.error("error creating user failed, no ID generated for" + accountHolder);
                throw new TransactionException("user was not created for " + accountHolder, TransactionException.ResponseCode.FAILURE.name());
            }
        } catch (SQLException e) {
            log.error(" failed creating user " + accountHolder);

            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                throw new TransactionException("failed to rollback transaction for user " + accountHolder, TransactionException.ResponseCode.FAILURE.name(), re);
            }

            throw new TransactionException("error when creating user " + accountHolder, TransactionException.ResponseCode.FAILURE.name());
        } finally {
            DbUtils.closeQuietly(conn, statement, generatedKeys);
        }
    }

    @Override
    public AccountHolder update(AccountHolder accountHolder) throws TransactionException {
        Connection conn = null;
        PreparedStatement statement = null;
        PreparedStatement lockStatement = null;
        ResultSet rs = null;
        AccountHolder targetAccountHolder = null;
        try {
            conn = RepositoryFactory.getConnection();
            conn.setAutoCommit(false);

            // lock account for writing:
            lockStatement = conn.prepareStatement(SQL_LOCK_ACCOUNT_BY_ACCOUNT_ID);
            lockStatement.setLong(1, accountHolder.getId());
            rs = lockStatement.executeQuery();
            if (rs.next()) {

                targetAccountHolder = new AccountHolder(
                        rs.getString(AccountHolderId),
                        rs.getString(FirstName),
                        rs.getString(LastName));

                if (log.isDebugEnabled())
                    log.debug("locked account of " + targetAccountHolder);
            }

            if (targetAccountHolder == null) {
                throw new TransactionException("failed to lock account " + targetAccountHolder.getAccountHolderId(), TransactionException.ResponseCode.FAILURE.name());
            }

            statement = conn.prepareStatement(SQL_UPDATE_ACCOUNT_HOLDER);
            statement.setString(1, accountHolder.getFirstName());
            statement.setString(2, accountHolder.getLastName());
            statement.setString(3, accountHolder.getAccountHolderId());
            statement.setLong(4, accountHolder.getId());

            int rowsAffected = statement.executeUpdate();

            conn.commit();
            if(rowsAffected == 0)
                return null;
            else
                return accountHolder;

        } catch (SQLException e) {
            log.error("error updating user " + accountHolder.getAccountHolderId());
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                throw new TransactionException("failed to rollback transaction for user " + accountHolder.getAccountHolderId(), TransactionException.ResponseCode.FAILURE.name(), re);
            }
            throw new TransactionException("error updating user " + accountHolder.getAccountHolderId(), TransactionException.ResponseCode.FAILURE.name());
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(statement);
        }
    }

    @Override
    public void delete(String accountHolderId) throws TransactionException {
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = RepositoryFactory.getConnection();
            statement = conn.prepareStatement(SQL_DELETE_ACCOUNT_HOLDER_BY_ACCOUNT_HOLDER_ID);
            statement.setString(1, accountHolderId);
            statement.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            log.error("error deleting user with id " + accountHolderId);

            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                throw new TransactionException("failed to rollback transaction for user with account holder id  " + accountHolderId, TransactionException.ResponseCode.FAILURE.name(), re);
            }

            throw new TransactionException("error when deleting user with  account holder id  " + accountHolderId, TransactionException.ResponseCode.FAILURE.name(), e);

        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(statement);
        }
    }

}
