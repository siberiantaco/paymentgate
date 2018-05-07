package ru.rsayadyan.paymentgate.web.dto;

import ru.rsayadyan.paymentgate.daos.model.enums.PaymentStatus;

import javax.validation.constraints.NotNull;

public class ChangePaymentRequest {

    @NotNull
    private PaymentStatus status;

    public ChangePaymentRequest(PaymentStatus status) {
        this.status = status;
    }

    public ChangePaymentRequest() {
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
