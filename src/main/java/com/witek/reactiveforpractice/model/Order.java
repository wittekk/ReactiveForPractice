package com.witek.reactiveforpractice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
   private String phoneNumber;
   private String orderNumber;
   private String productCode;
}
