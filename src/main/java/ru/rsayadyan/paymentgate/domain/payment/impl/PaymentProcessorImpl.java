package ru.rsayadyan.paymentgate.domain.payment.impl;

import ru.rsayadyan.paymentgate.daos.IPaymentRepository;
import ru.rsayadyan.paymentgate.domain.account.AccountCheckResult;
import ru.rsayadyan.paymentgate.domain.account.IAccountService;
import ru.rsayadyan.paymentgate.domain.payment.exception.PaymentProcessorException;
import ru.rsayadyan.paymentgate.daos.model.Payment;
import ru.rsayadyan.paymentgate.exception.DomainException;
import ru.rsayadyan.paymentgate.exception.ErrorCode;

public class PaymentProcessorImpl extends AbstractPaymentProcessor {

    private IAccountService accountService;

    public PaymentProcessorImpl(IPaymentRepository paymentRepository, IAccountService accountService) {
        this.paymentRepository = paymentRepository;
        this.accountService = accountService;
    }

    public Payment authorize(Payment payment) throws DomainException {
        final AccountCheckResult checkResult = accountService.check(payment.getAccIn(), payment.getAmount());

        if (checkResult == AccountCheckResult.ACCOUNT_DISABLED) {
            throw new PaymentProcessorException(ErrorCode.REPLENISH_ACCOUNT_DISABLED);
        }

        if (checkResult == AccountCheckResult.LIMIT_EXCEEDED) {
            throw new PaymentProcessorException(ErrorCode.REPLENISH_LIMIT_EXCEEDED);
        }

        final String holdId = accountService.hold(payment.getAccOut(), payment.getAmount());
        payment.authorize(holdId);
        return payment;
    }

    public Payment confirm(Payment payment) throws DomainException {
        accountService.transferTo(payment.getAccIn(), payment.getHoldId());
        payment.confirm();
        return payment;
    }
}
