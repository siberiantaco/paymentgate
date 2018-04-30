package ru.rsayadyan.paymentgate.exception;

public abstract class DomainException extends Exception {

    public abstract ErrorCode getErrorCode();


}
