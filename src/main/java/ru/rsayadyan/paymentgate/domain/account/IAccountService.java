package ru.rsayadyan.paymentgate.domain.account;

import ru.rsayadyan.paymentgate.domain.account.exception.AccountServiceException;

import java.math.BigInteger;

public interface IAccountService {

    public String hold(String outAccountId, BigInteger amount) throws AccountServiceException;

    public void transferTo(String inAccountId, String holdId) throws AccountServiceException;

    public AccountCheckResult check(String accountId, BigInteger amount) throws AccountServiceException;
}
