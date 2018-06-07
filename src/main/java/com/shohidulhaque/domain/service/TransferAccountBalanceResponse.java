package com.shohidulhaque.domain.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shohidulhaque.domain.valueobject.AccountTransferVO;

public class TransferAccountBalanceResponse {

    @JsonProperty
    final AccountTransferVO accountTransaction;
    @JsonProperty
    final int resultCount;

    public TransferAccountBalanceResponse(AccountTransferVO accountTransaction, int resultCount) {
        this.accountTransaction = accountTransaction;
        this.resultCount = resultCount;
    }

    public AccountTransferVO getAccountTransaction() {
        return accountTransaction;
    }

    public int getResultCount() {
        return resultCount;
    }
}
