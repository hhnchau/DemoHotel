package com.appromobile.hotel.payoo;

/**
 * Created by lam on 7/1/17.
 */

public class CreateOrderResponse {

  public String CheckSum;

  public String OrderInfo;

  public CreateOrderResponse(String checkSum, String orderInfo) {
    CheckSum = checkSum;
    OrderInfo = orderInfo;
  }

  public String getCheckSum() {
    return CheckSum;
  }

  public void setCheckSum(String checkSum) {
    CheckSum = checkSum;
  }

  public String getOrderInfo() {
    return OrderInfo;
  }

  public void setOrderInfo(String orderInfo) {
    OrderInfo = orderInfo;
  }
}
