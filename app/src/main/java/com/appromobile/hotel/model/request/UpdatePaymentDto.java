package com.appromobile.hotel.model.request;

/**
 * Created by xuan on 12/14/2016.
 */

public class UpdatePaymentDto {
    private String clientip;
    private int transactionId ;
    private String transactionId2;

    public String getTransactionId2() {
        return transactionId2;
    }

    public void setTransactionId2(String transactionId2) {
        this.transactionId2 = transactionId2;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getClientip() {
        return clientip;
    }

    public void setClientip(String clientip) {
        this.clientip = clientip;
    }
}
