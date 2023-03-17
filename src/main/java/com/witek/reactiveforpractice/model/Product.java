package com.witek.reactiveforpractice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String productId;
    private String productCode;
    private String productName;
    private double score;
}
