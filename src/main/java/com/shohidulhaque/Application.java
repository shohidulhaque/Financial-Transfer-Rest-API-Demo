package com.shohidulhaque;

import com.shohidulhaque.domain.repository.RepositoryFactory;
import com.shohidulhaque.domain.service.ServiceExceptionMapper;
import com.shohidulhaque.domain.service.TransactionService;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Main Class (Starting point) 
 */
public class Application {

	static Logger log = Logger.getLogger(Application.class);

	public static final String JERSEY_CONFIG_CLASS_NAMES =  "jersey.config.server.provider.classnames";
	public static final String SQL_DATA_FILE_PATH = "/demo.sql";

	public static final String H2_DRIVER = "org.h2.Driver";
	public static final String H2_CONNECTION_URL = "jdbc:h2:mem:moneyappTest;DB_CLOSE_DELAY=-1";
	public static final String H2_NAME = "sa";
	public static final String H2_PASSWORD = "sa";

	public static void main(String[] args) throws Exception {
		// Initialize H2 database with demo data
		log.info("Initialize demo .....");
		RepositoryFactory h2DaoFactory = RepositoryFactory.getRepositoryFactory();
		h2DaoFactory.initialiseDatabase(Application.class.getResourceAsStream(SQL_DATA_FILE_PATH));
		log.info("Initialisation Complete....");
		// Host service on jetty
		startService();
	}

	private static void startService() throws Exception {

		Server server = new Server(8080);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
		servletHolder.setInitParameter(JERSEY_CONFIG_CLASS_NAMES,
											    TransactionService.class.getCanonicalName() + "," +
												ServiceExceptionMapper.class.getCanonicalName());
		try
		{
			server.start();server.join();
		}
		finally
		{
			server.destroy();
		}

	}

}
