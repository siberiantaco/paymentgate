package ru.rsayadyan.paymentgate.exception;

public enum ErrorCode {

    WITHDRAW_ACCOUNT_DISABLED(1, ""),
    WITHDRAW_LIMIT_EXCEEDED(2, ""),
    WITHDRAW_NO_FUNDS(3, ""),
    WITHDRAW_NO_ACCOUNT(4, ""),

    REPLENISH_ACCOUNT_DISABLED(5, ""),
    REPLENISH_NO_ACCOUNT(6, ""),
    REPLENISH_LIMIT_EXCEEDED(7, ""),

    NO_SUCH_WITHHOLDING(8, "");



    private int code;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    private String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }



}
