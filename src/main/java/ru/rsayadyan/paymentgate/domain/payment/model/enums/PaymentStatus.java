package ru.rsayadyan.paymentgate.domain.payment.model.enums;

public enum PaymentStatus {
    INITIAL, PENDING_AUTH, AUTHORIZED, PENDING_CONFIRM, CONFIRMED, ERROR
}
