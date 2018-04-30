package ru.rsayadyan.paymentgate.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.rsayadyan.paymentgate.domain.payment.model.enums.PaymentStatus;

import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDto {

    public PaymentDto() {
    }

    public PaymentDto(String id, String accIn, String accOut, BigInteger amount, PaymentStatus status, Integer errorReason) {
        this.id = id;
        this.accIn = accIn;
        this.accOut = accOut;
        this.amount = amount;
        this.status = status;
        this.errorReason = errorReason;
    }

    private String id;

    private String accIn;

    private String accOut;

    private BigInteger amount;

    private PaymentStatus status;

    public String getId() {
        return id;
    }

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

    public Integer getErrorReason() {
        return errorReason;
    }

    private Integer errorReason;



}
