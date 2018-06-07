package com.shohidulhaque.domain.repository;

import com.shohidulhaque.Application;
import com.shohidulhaque.domain.repository.impl.AccountRepositoryImpl;
import com.shohidulhaque.domain.repository.impl.AccountHolderRepositoryImpl;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.h2.tools.RunScript;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A factory for creating and accessing repository services.
 * This factory will initialise a embedded h2 database and provides functionality for adding
 */
public class RepositoryFactory {
	public static final String H2_DRIVER = Application.H2_DRIVER;

	public static final String H2_CONNECTION_URL = Application.H2_CONNECTION_URL;

	public static final String H2_USER = Application.H2_NAME;

	public static final String H2_PASSWORD = Application.H2_PASSWORD;

	public static Logger log = Logger.getLogger(RepositoryFactory.class);

	public final AccountHolderRepository accountHolderRepository = new AccountHolderRepositoryImpl();
	public final AccountRepository accountRepository = new AccountRepositoryImpl();

	public static RepositoryFactory getRepositoryFactory() {
		return new RepositoryFactory();
	}

	RepositoryFactory() {
		DbUtils.loadDriver(H2_DRIVER);
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(H2_CONNECTION_URL, H2_USER, H2_PASSWORD);

	}

	public AccountHolderRepository getAccountHolderRepository() {
		return accountHolderRepository;
	}

	public AccountRepository getAccountRepository() {
		return accountRepository;
	}


	public void initialiseDatabase(InputStream in) {
		log.info("initialising database.");
		Connection conn = null;
		try(InputStreamReader inputStreamReader = new InputStreamReader(in))
		{
			conn = RepositoryFactory.getConnection();
			RunScript.execute(conn, inputStreamReader);
		}
		catch (SQLException e)
		{
			log.error("error initialising database with script sql file.", e);
			throw new RuntimeException(e);
		}
		catch (IOException e){
			log.error("error initialising database with script sql file.", e);
			throw new RuntimeException(e);
		}
		finally {
			DbUtils.closeQuietly(conn);
		}
	}

}
