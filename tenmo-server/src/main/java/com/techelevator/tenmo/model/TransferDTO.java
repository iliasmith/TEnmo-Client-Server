package com.techelevator.tenmo.model;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

public class TransferDTO {

    private long payeeId;
    private String payerUsername;
    @Min(value = 1, message = "Amount to transfer must be greater than zero.")
    private BigDecimal amount;

    public long getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(long payeeId) {
        this.payeeId = payeeId;
    }

    public String getPayerUsername() {
        return payerUsername;
    }

    public void setPayeeUsername(String payeeUsername) {
        this.payerUsername = payeeUsername;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
