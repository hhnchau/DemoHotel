package com.appromobile.hotel.payoo;


import vn.payoo.paymentsdk.data.converter.OrderConverter;
import vn.payoo.paymentsdk.data.model.Order;

public class PayooConverter implements OrderConverter {

  public static PayooConverter create() {
    return new PayooConverter();
  }

  @Override
  public <T> Order convert(T data) {
    CreateOrderResponse response = CreateOrderResponse.class.cast(data);
    return Order.newBuilder()
        .checksum(response.getCheckSum())
        .orderInfo(response.getOrderInfo())
        .build();
  }
}