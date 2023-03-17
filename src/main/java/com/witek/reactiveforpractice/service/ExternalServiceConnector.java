package com.witek.reactiveforpractice.service;

import com.witek.reactiveforpractice.model.Order;
import com.witek.reactiveforpractice.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;


@Component
public class ExternalServiceConnector {

    @Value("${productService.productServicePath}")
    private String productServicePath;

    @Value("${orderService.orderServicePath}")
    private String orderServicePath;

    private final WebClient productServiceWebClient;

    private final WebClient orderServiceWebClient;

    public ExternalServiceConnector(WebClient productServiceWebClient, WebClient orderServiceWebClient) {
        this.productServiceWebClient = productServiceWebClient;
        this.orderServiceWebClient = orderServiceWebClient;
    }

    public Flux<Order> getOrdersFromExternalService(String phoneNumber) {
        return orderServiceWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(orderServicePath)
                        .queryParam("phoneNumber", phoneNumber)
                        .build())
                .retrieve()
                .bodyToFlux(Order.class);
    }

    public Flux<Product> getProductsFromExternalService(String productCode) {
        return productServiceWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(productServicePath)
                        .queryParam("productCode", productCode)
                        .build())
                .retrieve()
                .bodyToFlux(Product.class);
    }
}
