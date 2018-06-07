package com.shohidulhaque.domain.service;

import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.valueobject.UserTransactionResponseVO;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Date;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<TransactionException> {

    public ServiceExceptionMapper() {
    }

    @Override
    public Response toResponse(TransactionException transactionException) {
        UserTransactionResponseVO userTransactionResponse = new UserTransactionResponseVO(transactionException.getResponseCode(),
                new Date(),
                transactionException.getFromAccountNumber(),
                transactionException.getToAccountNumber(),
                transactionException.getAmount());
        return Response.status(Response.Status.PRECONDITION_FAILED).entity(userTransactionResponse).type(MediaType.APPLICATION_JSON).build();
    }

}
