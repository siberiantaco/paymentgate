package ru.rsayadyan.paymentgate.web.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;

public class CreatePaymentRequest {

    public CreatePaymentRequest() {
    }

    public CreatePaymentRequest(String accIn, String accOut, BigInteger amount) {
        this.accIn = accIn;
        this.accOut = accOut;
        this.amount = amount;
    }

    @NotNull
    String accIn;

    @NotNull
    String accOut;

    @NotNull
    @Min(value=1)
    BigInteger amount;

    public String getAccIn() {
        return accIn;
    }

    public String getAccOut() {
        return accOut;
    }

    public BigInteger getAmount() {
        return amount;
    }

}
