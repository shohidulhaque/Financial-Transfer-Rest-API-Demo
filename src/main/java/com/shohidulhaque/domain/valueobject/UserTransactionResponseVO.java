package com.shohidulhaque.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.math.BigDecimal;
import java.util.Date;

public class UserTransactionResponseVO {

    public UserTransactionResponseVO(String responseCode,
                                            Date timestampOfTransfer,
                                            String fromAccountNumber,
                                            String toAccountNumber,
                                            BigDecimal amount) {
        this.responseCode = responseCode;
        this.timestampOfTransfer = timestampOfTransfer;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
    }

    @JsonProperty
    String responseCode;

    @JsonProperty
    Date timestampOfTransfer;


    @JsonProperty
    String fromAccountNumber;

    @JsonProperty
    String toAccountNumber;

    @JsonProperty
    BigDecimal amount;

    @Override
    public String toString() {
        return "UserTransactionResponseVO{" +
                "responseCode='" + responseCode + '\'' +
                ", timestampOfTransfer=" + timestampOfTransfer +
                ", fromAccountNumber='" + fromAccountNumber + '\'' +
                ", toAccountNumber='" + toAccountNumber + '\'' +
                ", amount=" + amount +
                '}';
    }
}
