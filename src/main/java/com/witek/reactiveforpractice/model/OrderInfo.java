package com.witek.reactiveforpractice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OrderInfo {
    private String orderNumber;
    private String userName;
    private String phoneNumber;
    private String productCode;
    private String productName;
    private String productId;

}
