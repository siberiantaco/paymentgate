package ru.rsayadyan.paymentgate.domain.payment.exception;

import ru.rsayadyan.paymentgate.exception.DomainException;
import ru.rsayadyan.paymentgate.exception.ErrorCode;

public class PaymentProcessorException extends DomainException {
    public PaymentProcessorException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    private ErrorCode errorCode;

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
