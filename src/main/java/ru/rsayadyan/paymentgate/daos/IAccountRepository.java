package ru.rsayadyan.paymentgate.daos;

import ru.rsayadyan.paymentgate.daos.model.Account;

public interface IAccountRepository {

    public Account get(String accId);

    public void update(Account account);
}
