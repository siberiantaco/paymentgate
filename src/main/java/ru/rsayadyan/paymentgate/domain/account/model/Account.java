package ru.rsayadyan.paymentgate.domain.account.model;

import java.math.BigInteger;

public class Account {

    public Account(String id, Boolean enabled, BigInteger amount, BigInteger transferLimit, BigInteger holdenAmount) {
        this.id = id;
        this.enabled = enabled;
        this.amount = amount;
        this.transferLimit = transferLimit;
        this.holdenAmount = holdenAmount;
    }

    public String getId() {
        return id;
    }

    private String id;

    public Boolean isEnabled() {
        return enabled;
    }

    private Boolean enabled;

    public BigInteger getAmount() {
        return amount;
    }

    private BigInteger amount;

    public BigInteger getTransferLimit() {
        return transferLimit;
    }

    private BigInteger transferLimit;

    public BigInteger getHoldenAmount() {
        return holdenAmount;
    }

    private BigInteger holdenAmount;

    public void hold(Withholding withholding) {
        this.holdenAmount = this.holdenAmount.add(withholding.getAmount());
    }

    public void withdraw(Withholding withholding) {
        this.holdenAmount = this.holdenAmount.subtract(withholding.getAmount());
        this.amount = this.amount.subtract(withholding.getAmount());
    }

    public void replenish(Withholding withholding) {
        this.amount = this.amount.add(withholding.getAmount());
    }


}
