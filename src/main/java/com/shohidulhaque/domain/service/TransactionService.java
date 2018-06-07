package com.shohidulhaque.domain.service;

import com.shohidulhaque.domain.exception.TransactionException;
import com.shohidulhaque.domain.repository.RepositoryFactory;
import com.shohidulhaque.domain.valueobject.UserTransactionResponseVO;
import com.shohidulhaque.domain.valueobject.UserTransactionVO;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/transaction/v1/")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionService {

	private final RepositoryFactory repositoryFactory = RepositoryFactory.getRepositoryFactory();

	/**
	 * Transfer fund between two accounts.
	 * @param transaction the transaction to be performed.
	 * @return Response the result of attempting to process user transaction
	 * @throws TransactionException if there are problems when a fund transfer is being performed.
	 * @throws WebApplicationException if there is an error during a transaction.
	 */
	@POST
	public Response transferFund(UserTransactionVO transaction) throws TransactionException, WebApplicationException {

			TransferAccountBalanceResponse update = repositoryFactory.getAccountRepository().transferAccountBalance(transaction);
			if (update.getResultCount() == 2)
			{
				UserTransactionResponseVO userTransactionResponse =  new UserTransactionResponseVO(	TransactionException.ResponseCode.SUCCESS.name(),
																			update.getAccountTransaction().getTransactionTime(),
																			update.getAccountTransaction().getFromAccountNumber(),
																			update.getAccountTransaction().getToAccountNumber(),
																			update.getAccountTransaction().getAmount());

				return Response.status(Response.Status.OK)
								.entity(userTransactionResponse)
								.type(MediaType.APPLICATION_JSON)
								.build();

			}
			else
				{
				// transaction failed
				throw new WebApplicationException("Transaction failed", Response.Status.BAD_REQUEST);
			}
	}

}
