package ru.rsayadyan.paymentgate.domain.account.exception;

import ru.rsayadyan.paymentgate.exception.DomainException;
import ru.rsayadyan.paymentgate.exception.ErrorCode;

public class AccountServiceException extends DomainException {

    public AccountServiceException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    private ErrorCode errorCode;

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
