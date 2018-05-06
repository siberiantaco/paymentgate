package ru.rsayadyan.paymentgate.domain.payment.model;

import ru.rsayadyan.paymentgate.exception.ErrorCode;
import ru.rsayadyan.paymentgate.domain.payment.model.enums.PaymentStatus;

import java.math.BigInteger;
import java.util.UUID;

public class Payment {

    public String getAccIn() {
        return accIn;
    }

    public String getAccOut() {
        return accOut;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getHoldId() {
        return holdId;
    }


    public Payment(String accIn, String accOut, BigInteger amount) {
        this.id = UUID.randomUUID().toString();
        this.accIn = accIn;
        this.accOut = accOut;
        this.amount = amount;
        this.status = PaymentStatus.INITIAL;
    }

    public Integer getErrorReason() {
        return errorReason;
    }

    public Payment(String id, String accIn, String accOut, BigInteger amount, PaymentStatus status, String holdId, Integer errorReason) {
        this.id = id;
        this.accIn = accIn;
        this.accOut = accOut;
        this.amount = amount;
        this.status = status;
        this.holdId = holdId;
        this.errorReason = errorReason;
    }

    public void authorize(String holdId) {
        this.status = PaymentStatus.AUTHORIZED;
        this.holdId = holdId;
    }

    public void confirm() {
        this.status = PaymentStatus.CONFIRMED;
    }

    public void toError(ErrorCode errorCode) {
        this.status = PaymentStatus.ERROR;
        this.errorReason = errorCode.getCode();
    }

    public void toPendingAuth() {
        this.status = PaymentStatus.PENDING_AUTH;
    }

    public void toPendingConfirm() {
        this.status = PaymentStatus.PENDING_CONFIRM;
    }


    private String id;

    private String accIn;

    private String accOut;

    private BigInteger amount;

    private PaymentStatus status;

    private String holdId;

    private Integer errorReason;


}
