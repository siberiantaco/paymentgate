package ru.rsayadyan.paymentgate.daos.impl.exception;

public class RepositoryException extends RuntimeException {

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
