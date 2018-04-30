package ru.rsayadyan.paymentgate.daos;

import ru.rsayadyan.paymentgate.domain.account.model.Account;

public interface IAccountRepository {

    public Account get(String accId);

    public void update(Account account);
}
