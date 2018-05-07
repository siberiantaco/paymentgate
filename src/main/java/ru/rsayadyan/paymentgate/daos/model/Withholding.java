package ru.rsayadyan.paymentgate.daos.model;

import ru.rsayadyan.paymentgate.exception.ErrorCode;
import ru.rsayadyan.paymentgate.domain.account.exception.AccountServiceException;

import java.math.BigInteger;
import java.util.UUID;

public class Withholding {

    public Withholding(String id, BigInteger amount, String accountId) {
        this.id = id;
        this.amount = amount;
        this.accountId = accountId;
    }

    public String getId() {
        return id;
    }

    private String id;

    public BigInteger getAmount() {
        return amount;
    }

    private BigInteger amount;

    public String getAccountId() {
        return accountId;
    }

    private String accountId;


    public Withholding(BigInteger amount, Account account) throws AccountServiceException {
        if (account.getAmount().subtract(account.getHoldenAmount()).compareTo(amount) < 0) {
            throw new AccountServiceException(ErrorCode.WITHDRAW_NO_FUNDS);
        }

        if (!account.isEnabled()) {
            throw new AccountServiceException(ErrorCode.WITHDRAW_ACCOUNT_DISABLED);
        }

        if(account.getTransferLimit().compareTo(amount) < 0) {
            throw new AccountServiceException(ErrorCode.WITHDRAW_LIMIT_EXCEEDED);
        }

        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.accountId = account.getId();
    }


}
