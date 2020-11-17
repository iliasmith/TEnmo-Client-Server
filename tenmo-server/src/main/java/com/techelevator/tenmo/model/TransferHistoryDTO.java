package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferHistoryDTO {

    private long transferId;
    private int transferType;
    private String usernameFrom;
    private String usernameTo;
    private BigDecimal amount;
    private String usernameOfCurrentUser;

    public long getTransferId() {
        return transferId;
    }

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

    public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getUsernameFrom() {
        return usernameFrom;
    }

    public void setUsernameFrom(String usernameFrom) {
        this.usernameFrom = usernameFrom;
    }

    public String getUsernameTo() {
        return usernameTo;
    }

    public void setUsernameTo(String usernameTo) {
        this.usernameTo = usernameTo;
    }

    public String getUsernameOfCurrentUser() {
        return usernameOfCurrentUser;
    }

    public void setUsernameOfCurrentUser(String usernameOfCurrentUser) {
        this.usernameOfCurrentUser = usernameOfCurrentUser;
    }
}
