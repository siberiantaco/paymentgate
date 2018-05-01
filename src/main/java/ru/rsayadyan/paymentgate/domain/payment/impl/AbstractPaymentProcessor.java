package ru.rsayadyan.paymentgate.domain.payment.impl;

import ru.rsayadyan.paymentgate.daos.IPaymentRepository;
import ru.rsayadyan.paymentgate.domain.payment.IPaymentProcessor;
import ru.rsayadyan.paymentgate.domain.payment.exception.PaymentNotFoundException;
import ru.rsayadyan.paymentgate.domain.payment.model.Payment;
import ru.rsayadyan.paymentgate.domain.payment.model.enums.PaymentStatus;
import ru.rsayadyan.paymentgate.exception.DomainException;

import java.math.BigInteger;

public abstract class AbstractPaymentProcessor implements IPaymentProcessor {

    protected IPaymentRepository paymentRepository;

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

    public abstract Payment authorize(Payment payment) throws DomainException;

    public abstract Payment confirm(Payment payment) throws DomainException;
}
