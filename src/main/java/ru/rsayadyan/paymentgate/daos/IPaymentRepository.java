package ru.rsayadyan.paymentgate.daos;

import ru.rsayadyan.paymentgate.domain.payment.model.Payment;

public interface IPaymentRepository {

    public void save(Payment payment);

    public Payment get(String paymentId);

    public void update(Payment payment);
}
