package com.shohidulhaque.domain.repository;

import com.shohidulhaque.domain.exception.TransactionException;
import java.util.List;

public interface CRUDRepository<PK, MODEL> {
        List<MODEL> findAll() throws TransactionException;
        MODEL create(MODEL model) throws TransactionException;
        MODEL findByPK(PK pk) throws TransactionException;
        MODEL update(MODEL model) throws TransactionException;
        void delete(PK key) throws TransactionException;
}
