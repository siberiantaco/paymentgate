package ru.rsayadyan.paymentgate.factory;

import ru.rsayadyan.paymentgate.daos.IAccountRepository;
import ru.rsayadyan.paymentgate.daos.IPaymentRepository;
import ru.rsayadyan.paymentgate.daos.IWithholdingRepository;
import ru.rsayadyan.paymentgate.daos.impl.AccountRepository;
import ru.rsayadyan.paymentgate.daos.impl.PaymentRepository;
import ru.rsayadyan.paymentgate.daos.impl.WithholdingRepository;
import ru.rsayadyan.paymentgate.domain.account.IAccountService;
import ru.rsayadyan.paymentgate.domain.account.impl.AccountServiceImpl;
import ru.rsayadyan.paymentgate.domain.payment.IPaymentProcessor;
import ru.rsayadyan.paymentgate.domain.payment.impl.PaymentProcessorImpl;

public class PaymentProcessorFactory {

    private static IPaymentProcessor paymentProcessor;

    private static IAccountRepository accountRepository;

    private static IWithholdingRepository withholdingRepository;

    private static IPaymentRepository paymentRepository;

    private static IAccountService accountService;

    private static IWithholdingRepository getWithholdingRepository() {
        if (withholdingRepository == null)
            withholdingRepository = new WithholdingRepository();
        return withholdingRepository;
    }

    private static IAccountRepository getAccountRepository() {
        if (accountRepository == null)
            accountRepository = new AccountRepository();
        return accountRepository;
    }

    private static IPaymentRepository getPaymentRepository() {
        if (paymentRepository == null)
            paymentRepository = new PaymentRepository();
        return paymentRepository;
    }

    private static IAccountService getAccountService() {
        if (accountService == null) {
            accountService = new AccountServiceImpl(getAccountRepository(), getWithholdingRepository());
        }
        return accountService;
    }

    public static IPaymentProcessor getPaymentProcessor() {
        if (paymentProcessor == null) {
            paymentProcessor = new PaymentProcessorImpl(getPaymentRepository(), getAccountService());
        }

        return paymentProcessor;
    }

}
