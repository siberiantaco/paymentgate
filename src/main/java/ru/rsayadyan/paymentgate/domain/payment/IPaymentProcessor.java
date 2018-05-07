package ru.rsayadyan.paymentgate.domain.payment;

import ru.rsayadyan.paymentgate.daos.model.enums.PaymentStatus;
import ru.rsayadyan.paymentgate.daos.model.Payment;

import java.math.BigInteger;

public interface IPaymentProcessor {

    public Payment initPayment(String accIn, String accOut, BigInteger amount);

    public Payment promoteTo(String paymentId, PaymentStatus status);

    public Payment get(String paymentId);
}
