package com.shohidulhaque.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.shohidulhaque.Application;
import com.shohidulhaque.domain.repository.RepositoryFactory;
import com.shohidulhaque.domain.service.ServiceExceptionMapper;
import com.shohidulhaque.domain.service.TransactionService;
import com.shohidulhaque.domain.valueobject.UserTransactionVO;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTransactionService {

    protected static Server server = null;
    protected static PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    protected static HttpClient client;
    protected static RepositoryFactory repositoryFactory = RepositoryFactory.getRepositoryFactory();
    protected ObjectMapper mapper = new ObjectMapper();
    protected URIBuilder builder = new URIBuilder().setScheme("http").setHost("localhost:8084");

    @BeforeClass
    public static void setup() throws Exception {
        repositoryFactory.initialiseDatabase(TestTransactionService.class.getResourceAsStream(Application.SQL_DATA_FILE_PATH));
        startServer();
        connManager.setDefaultMaxPerRoute(10);
        connManager.setMaxTotal(20);
        client = HttpClients.custom()
                .setConnectionManager(connManager)
                .setConnectionManagerShared(true)
                .build();
    }

    @AfterClass
    public static void closeClient() throws Exception {
        HttpClientUtils.closeQuietly(client);
    }


    private static void startServer() throws Exception {
        if (server == null) {
            server = new Server(8084);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);
            ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
            servletHolder.setInitParameter(Application.JERSEY_CONFIG_CLASS_NAMES,
                    ServiceExceptionMapper.class.getCanonicalName() + "," +
                            TransactionService.class.getCanonicalName());
            server.start();
        }
    }

    @Test
    public void testTransactionEnoughFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transaction/v1/").build();
        BigDecimal amount = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        UserTransactionVO transaction = new UserTransactionVO(amount, "31223123", "21223123");

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(response.getEntity().toString());
        assertEquals("the wrong status code was returned.",200, statusCode);
    }

    @Test
    public void testTransactionNotEnoughFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transaction/v1/").build();
        BigDecimal amount = new BigDecimal(10000000).setScale(4, RoundingMode.HALF_EVEN);
        UserTransactionVO transaction = new UserTransactionVO(amount, "31223123", "21223123");

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals("the wrong status code was returned.",Response.Status.PRECONDITION_FAILED.getStatusCode(), statusCode);
    }


}
