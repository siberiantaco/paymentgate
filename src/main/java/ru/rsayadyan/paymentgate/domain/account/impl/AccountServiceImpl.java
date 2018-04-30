package ru.rsayadyan.paymentgate.domain.account.impl;

import ru.rsayadyan.paymentgate.daos.IAccountRepository;
import ru.rsayadyan.paymentgate.daos.IWithholdingRepository;
import ru.rsayadyan.paymentgate.daos.TransactionManager;
import ru.rsayadyan.paymentgate.domain.account.AccountCheckResult;
import ru.rsayadyan.paymentgate.domain.account.IAccountService;
import ru.rsayadyan.paymentgate.domain.account.exception.AccountServiceException;
import ru.rsayadyan.paymentgate.domain.account.model.Account;
import ru.rsayadyan.paymentgate.domain.account.model.Withholding;
import ru.rsayadyan.paymentgate.exception.ErrorCode;

import java.math.BigInteger;

public class AccountServiceImpl implements IAccountService {

    IAccountRepository accountRepository;

    IWithholdingRepository withholdingRepository;

    public AccountServiceImpl(IAccountRepository accountRepository, IWithholdingRepository withholdingRepository) {
        this.accountRepository = accountRepository;
        this.withholdingRepository = withholdingRepository;
    }



    public String hold(String outAccountId, BigInteger amount) throws AccountServiceException {

        try {

            TransactionManager.startTransaction();

            final Account account = accountRepository.get(outAccountId);

            if (account == null) {
                throw new AccountServiceException(ErrorCode.WITHDRAW_NO_ACCOUNT);
            }

            final Withholding withholding = new Withholding(amount, account);
            withholdingRepository.save(withholding);

            account.hold(withholding);
            accountRepository.update(account);

            TransactionManager.commit();

            return withholding.getId();

        } catch (Throwable e) {

            TransactionManager.rollback();
            throw e;

        }

    }

    public void transferTo(String inAccountId, String holdId) throws AccountServiceException {

        try {

            TransactionManager.startTransaction();

            final Withholding withholding = withholdingRepository.get(holdId);

            if (withholding == null) {
                throw new AccountServiceException(ErrorCode.NO_SUCH_WITHHOLDING);
            }

            final Account outAccount = accountRepository.get(withholding.getAccountId());
            outAccount.withdraw(withholding);
            accountRepository.update(outAccount);

            final Account inAccount = accountRepository.get(inAccountId);
            inAccount.replenish(withholding);
            accountRepository.update(inAccount);

            withholdingRepository.delete(withholding);

            TransactionManager.commit();

        } catch (Throwable e) {

            TransactionManager.rollback();
            throw e;

        }

    }

    public AccountCheckResult check(String accountId, BigInteger amount) throws AccountServiceException {
        return null;
    }
}
