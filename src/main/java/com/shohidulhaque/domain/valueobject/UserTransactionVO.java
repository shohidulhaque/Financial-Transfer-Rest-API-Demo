package com.shohidulhaque.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * A financial transaction request from a user.
 */
public class UserTransactionVO {

	/*needed for the json mapping libraries used.*/
	public UserTransactionVO() {
	}


	@JsonProperty(required = true)
	private BigDecimal amount;

	@JsonProperty(required = true)
	private String fromAccountNumber;

	@JsonProperty(required = true)
	private String toAccountNumber;

	public UserTransactionVO(BigDecimal amount, String fromAccountNumber, String toAccountNumber) {
		this.amount = amount;
		this.fromAccountNumber = fromAccountNumber;
		this.toAccountNumber = toAccountNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getFromAccountNumber() {
		return fromAccountNumber;
	}

	public String getToAccountNumber() {
		return toAccountNumber;
	}

}
