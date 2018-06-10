package com.shohidulhaque.domain.repository.impl;

import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.model.AccountHolder;
import com.shohidulhaque.domain.repository.AccountHolderRepository;
import com.shohidulhaque.domain.repository.RepositoryFactory;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AccountHolderRepositoryImpl implements AccountHolderRepository {

    private final static String SQL_GET_ACCOUNT_HOLDER_BY_ID = "SELECT * FROM AccountHolder WHERE Id = ? ";
    private final static String SQL_DELETE_ACCOUNT_HOLDER_BY_ID = "DELETE FROM AccountHolder WHERE Id = ? ";

    private final static String SQL_GET_ACCOUNT_HOLDER_BY_ACCOUNT_HOLDER_ID = "SELECT * FROM AccountHolder WHERE AccountHolderId = ? ";
    private final static String SQL_DELETE_ACCOUNT_HOLDER_BY_ACCOUNT_HOLDER_ID = "DELETE FROM AccountHolder WHERE AccountHolderId = ? ";
    private final static String SQL_GET_ALL_ACCOUNT_HOLDER = "SELECT * FROM AccountHolder";
    private final static String SQL_CREATE_ACCOUNT_HOLDER = "INSERT INTO AccountHolder (AccountHolderId, FirstName, LastName) VALUES (?,?,?)";
    private final static String SQL_UPDATE_ACCOUNT_HOLDER = "UPDATE AccountHolder SET FirstName = ?, LastName = ?, AccountHolderId = ? WHERE Id = ? ";

    private static Logger log = Logger.getLogger(AccountHolderRepositoryImpl.class);

    @Override
    public List<AccountHolder> findAll() throws TransactionException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<AccountHolder> users = new ArrayList<AccountHolder>();
        try {
            conn = RepositoryFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ALL_ACCOUNT_HOLDER);
            rs = stmt.executeQuery();
            while (rs.next()) {
                AccountHolder u = new AccountHolder(
                        rs.getLong("Id"),
                        rs.getString("AccountHolderId"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"));
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
                        rs.getLong("Id"),
                        rs.getString("AccountHolderId"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"));

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

        try {
            conn = RepositoryFactory.getConnection();
            conn.setAutoCommit(false);
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


//    @Override
  //  public AccountHolder updateAccountHolder(AccountHolder accountHolder) throws TransactionException {
   //     return null;
   // }


    /**
     * Find all users
     */
//    public List<AccountHolder> getAllAccountHolders() throws TransactionException {
//        Connection conn = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//        List<AccountHolder> users = new ArrayList<AccountHolder>();
//        try {
//            conn = RepositoryFactory.getConnection();
//            stmt = conn.prepareStatement(SQL_GET_ALL_ACCOUNT_HOLDER);
//            rs = stmt.executeQuery();
//            while (rs.next()) {
//                AccountHolder u = new AccountHolder(
//                        rs.getLong("Id"),
//                        rs.getString("AccountHolderId"),
//                        rs.getString("FirstName"),
//                        rs.getString("LastName"));
//                users.add(u);
//                if (log.isDebugEnabled())
//                    log.debug("retrieved account holder " + u);
//            }
//            return users;
//        } catch (SQLException e) {
//            throw new TransactionException("error getting users", TransactionException.ResponseCode.FAILURE.name(), e);
//        } finally {
//            DbUtils.closeQuietly(conn, stmt, rs);
//        }
//    }

    /**
     * Find user by userId
     */
//    public AccountHolder getAccountHolderById(long id) throws TransactionException {
//        Connection conn = null;
//        PreparedStatement statement = null;
//        ResultSet rs = null;
//        AccountHolder ah = null;
//        try {
//            conn = RepositoryFactory.getConnection();
//            statement = conn.prepareStatement(SQL_GET_ACCOUNT_HOLDER_BY_ID);
//            statement.setLong(1, id);
//            rs = statement.executeQuery();
//            if (rs.next()) {
//
//                ah = new AccountHolder(
//                        rs.getLong("Id"),
//                        rs.getString("AccountHolderId"),
//                        rs.getString("FirstName"),
//                        rs.getString("LastName"));
//
//                if (log.isDebugEnabled())
//                    log.debug("retrieved user " + ah);
//            }
//            return ah;
//        } catch (SQLException e) {
//            throw new TransactionException("error getting user data", TransactionException.ResponseCode.FAILURE.name(), e);
//        } finally {
//            DbUtils.closeQuietly(conn, statement, rs);
//        }
//    }

    /**
     * Find user by userName
     */
//    public AccountHolder getAccountHolderByAccountHolderId(String accountHolderId) throws TransactionException {
//        Connection conn = null;
//        PreparedStatement statement = null;
//        ResultSet rs = null;
//        AccountHolder ac = null;
//        try {
//            conn = RepositoryFactory.getConnection();
//            statement = conn.prepareStatement(SQL_GET_ACCOUNT_HOLDER_BY_ACCOUNT_HOLDER_ID);
//            statement.setString(1, accountHolderId);
//            rs = statement.executeQuery();
//            if (rs.next()) {
//                ac = new AccountHolder(
//                        rs.getLong("Id"),
//                        rs.getString("AccountHolderId"),
//                        rs.getString("FirstName"),
//                        rs.getString("LastName"));
//
//                if (log.isDebugEnabled())
//                    log.debug("Retrieve User: " + ac);
//            }
//            return ac;
//        } catch (SQLException e) {
//            throw new TransactionException("error reading user data", TransactionException.ResponseCode.FAILURE.name(), e);
//        } finally {
//            DbUtils.closeQuietly(conn, statement, rs);
//        }
//    }

    /**
     * Save User
     */
//    public long createAccountHolder(AccountHolder user) throws TransactionException {
//        Connection conn = null;
//        PreparedStatement statement = null;
//        ResultSet generatedKeys = null;
//        try {
//            conn = RepositoryFactory.getConnection();
//            conn.setAutoCommit(false);
//            statement = conn.prepareStatement(SQL_CREATE_ACCOUNT_HOLDER, Statement.RETURN_GENERATED_KEYS);
//            statement.setString(1, user.getAccountHolderId());
//            statement.setString(2, user.getFirstName());
//            statement.setString(3, user.getLastName());
//
//            int affectedRows = statement.executeUpdate();
//
//            if (affectedRows == 0) {
//                log.error(" failed creating user " + user);
//                throw new TransactionException("error when creating user " + user, TransactionException.ResponseCode.FAILURE.name());
//            }
//            generatedKeys = statement.getGeneratedKeys();
//            conn.commit();
//            if (generatedKeys.next()) {
//                user.setId(generatedKeys.getLong(1));
//                return user.getId();
//            } else {
//                log.error("error creating user failed, no ID generated for" + user);
//                throw new TransactionException("user was not created for " + user, TransactionException.ResponseCode.FAILURE.name());
//            }
//        } catch (SQLException e) {
//            log.error(" failed creating user " + user);
//
//            try {
//                if (conn != null)
//                    conn.rollback();
//            } catch (SQLException re) {
//                throw new TransactionException("failed to rollback transaction for user " + user, TransactionException.ResponseCode.FAILURE.name(), re);
//            }
//
//            throw new TransactionException("error when creating user " + user, TransactionException.ResponseCode.FAILURE.name());
//        } finally {
//            DbUtils.closeQuietly(conn, statement, generatedKeys);
//        }
//    }

    /**
     * Update User
     */
//    public int updateAccountHolder(Long userId, AccountHolder user) throws TransactionException {
//        Connection conn = null;
//        PreparedStatement statement = null;
//
//        try {
//            conn = RepositoryFactory.getConnection();
//            conn.setAutoCommit(false);
//            statement = conn.prepareStatement(SQL_UPDATE_ACCOUNT_HOLDER);
//            statement.setString(1, user.getAccountHolderId());
//            statement.setLong(2, userId);
//            int numberOfRows = statement.executeUpdate();
//            conn.commit();
//            return numberOfRows;
//        } catch (SQLException e) {
//            log.error("error updating user " + user);
//            try {
//                if (conn != null)
//                    conn.rollback();
//            } catch (SQLException re) {
//                throw new TransactionException("failed to rollback transaction for user " + user, TransactionException.ResponseCode.FAILURE.name(), re);
//            }
//            throw new TransactionException("error updating user " + user, TransactionException.ResponseCode.FAILURE.name());
//        } finally {
//            DbUtils.closeQuietly(conn);
//            DbUtils.closeQuietly(statement);
//        }
//    }

//    public int deleteAccountHolderByAccountHolderId(String accountHolderId) throws TransactionException {
//        Connection conn = null;
//        PreparedStatement statement = null;
//
//        try {
//            conn = RepositoryFactory.getConnection();
//            statement = conn.prepareStatement(SQL_DELETE_ACCOUNT_HOLDER_BY_ACCOUNT_HOLDER_ID);
//            statement.setString(1, accountHolderId);
//            int numberOfRows = statement.executeUpdate();
//            conn.commit();
//            return numberOfRows;
//        } catch (SQLException e) {
//            log.error("error deleting user with id " + accountHolderId);
//
//            try {
//                if (conn != null)
//                    conn.rollback();
//            } catch (SQLException re) {
//                throw new TransactionException("failed to rollback transaction for user with account holder id  " + accountHolderId, TransactionException.ResponseCode.FAILURE.name(), re);
//            }
//
//            throw new TransactionException("error when deleting user with  account holder id  " + accountHolderId, TransactionException.ResponseCode.FAILURE.name(), e);
//
//        } finally {
//            DbUtils.closeQuietly(conn);
//            DbUtils.closeQuietly(statement);
//        }
//    }

    /**
     * Delete User
     */
//    public int deleteAccountHolder(long userId) throws TransactionException {
//        Connection conn = null;
//        PreparedStatement statement = null;
//
//        try {
//            conn = RepositoryFactory.getConnection();
//            statement = conn.prepareStatement(SQL_DELETE_ACCOUNT_HOLDER_BY_ID);
//            statement.setLong(1, userId);
//            int numberOfRows = statement.executeUpdate();
//            conn.commit();
//            return numberOfRows;
//        } catch (SQLException e) {
//            log.error("error deleting user with id " + userId);
//
//            try {
//                if (conn != null)
//                    conn.rollback();
//            } catch (SQLException re) {
//                throw new TransactionException("failed to rollback transaction for user id " + userId, TransactionException.ResponseCode.FAILURE.name(), re);
//            }
//
//            throw new TransactionException("error when deleting user with id " + userId, TransactionException.ResponseCode.FAILURE.name(), e);
//
//        } finally {
//            DbUtils.closeQuietly(conn);
//            DbUtils.closeQuietly(statement);
//        }
//    }



}
