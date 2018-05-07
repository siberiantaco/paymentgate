package ru.rsayadyan.paymentgate.domain.payment.impl;

import ru.rsayadyan.paymentgate.daos.IPaymentRepository;
import ru.rsayadyan.paymentgate.daos.TransactionManager;
import ru.rsayadyan.paymentgate.domain.payment.IPaymentProcessor;
import ru.rsayadyan.paymentgate.domain.payment.exception.PaymentNotFoundException;
import ru.rsayadyan.paymentgate.daos.model.Payment;
import ru.rsayadyan.paymentgate.daos.model.enums.PaymentStatus;
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

        TransactionManager.startTransaction();
        final Payment payment = paymentRepository.get(paymentId);

        if (payment == null) {
            throw new PaymentNotFoundException();
        }

        try {
            switch (promotedStatus) {
                case AUTHORIZED:
                    if (payment.getStatus() == PaymentStatus.INITIAL) {

                        payment.toPendingAuth();
                        paymentRepository.update(payment);
                        TransactionManager.commit();

                        Payment authPayment = authorize(payment);
                        paymentRepository.update(authPayment);
                        return authPayment;
                    }
                    break;
                case CONFIRMED:
                    if (payment.getStatus() == PaymentStatus.AUTHORIZED) {

                        payment.toPendingConfirm();
                        paymentRepository.update(payment);
                        TransactionManager.commit();

                        Payment confirmedPayment = confirm(payment);
                        paymentRepository.update(confirmedPayment);
                        return confirmedPayment;
                    }
                    break;
            }
        } catch (DomainException ex) {
            payment.toError(ex.getErrorCode());
            paymentRepository.update(payment);
        } finally {
            TransactionManager.commit();
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
