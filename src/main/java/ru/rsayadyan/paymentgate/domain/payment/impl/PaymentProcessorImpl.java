package ru.rsayadyan.paymentgate.domain.payment.impl;

import ru.rsayadyan.paymentgate.daos.IPaymentRepository;
import ru.rsayadyan.paymentgate.domain.account.AccountCheckResult;
import ru.rsayadyan.paymentgate.domain.account.IAccountService;
import ru.rsayadyan.paymentgate.domain.payment.IPaymentProcessor;
import ru.rsayadyan.paymentgate.domain.payment.exception.PaymentNotFoundException;
import ru.rsayadyan.paymentgate.domain.payment.exception.PaymentProcessorException;
import ru.rsayadyan.paymentgate.domain.payment.model.Payment;
import ru.rsayadyan.paymentgate.domain.payment.model.enums.PaymentStatus;
import ru.rsayadyan.paymentgate.exception.DomainException;
import ru.rsayadyan.paymentgate.exception.ErrorCode;

import java.math.BigInteger;

public class PaymentProcessorImpl implements IPaymentProcessor {

    IPaymentRepository paymentRepository;

    IAccountService accountService;


    public PaymentProcessorImpl(IPaymentRepository paymentRepository, IAccountService accountService) {
        this.paymentRepository = paymentRepository;
        this.accountService = accountService;
    }


    public Payment initPayment(String accIn, String accOut, BigInteger amount) {
        final Payment payment = new Payment(accIn, accOut, amount);
        paymentRepository.save(payment);
        return payment;
    }

    public Payment promoteTo(String paymentId, PaymentStatus promotedStatus) {
        final Payment payment = paymentRepository.get(paymentId);
        if (payment == null) {
            throw new PaymentNotFoundException();
        }
        try {
            switch (promotedStatus) {
                case AUTHORIZED:
                    return payment.getStatus() == PaymentStatus.INITIAL ?
                            authorize(payment) :
                            payment;
                case CONFIRMED:
                    return payment.getStatus() == PaymentStatus.AUTHORIZED ?
                            confirm(payment) :
                            payment;
            }
        } catch (DomainException ex) {
            payment.toError(ex.getErrorCode());
        } finally {
            paymentRepository.update(payment);

        }
        return payment;
    }

    public Payment get(String paymentId) {
        final Payment payment = paymentRepository.get(paymentId);
        if (payment == null) {
            throw new PaymentNotFoundException();
        }
        return payment;
    }

    private Payment authorize(Payment payment) throws DomainException {
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

    private Payment confirm(Payment payment) throws DomainException {
        accountService.transferTo(payment.getAccIn(), payment.getHoldId());
        payment.confirm();
        return payment;
    }
}
