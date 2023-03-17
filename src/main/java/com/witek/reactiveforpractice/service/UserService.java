package com.witek.reactiveforpractice.service;

import com.witek.reactiveforpractice.model.Order;
import com.witek.reactiveforpractice.model.OrderInfo;
import com.witek.reactiveforpractice.model.Product;
import com.witek.reactiveforpractice.model.Users;
import com.witek.reactiveforpractice.repository.UserInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserInfoRepository userInfoRepository;

    private final ExternalServiceConnector externalServiceConnector;

    private String requestId;

    public UserService(UserInfoRepository userInfoRepository, ExternalServiceConnector externalServiceConnector) {
        this.userInfoRepository = userInfoRepository;
        this.externalServiceConnector = externalServiceConnector;
    }

    public Flux<OrderInfo> getOrderInfo(String userId, String userRequestId) {
        requestId = userRequestId;

        Mono<Users> user = getUser(userId);

        Flux<Order> orders = user.flatMapMany(u -> getOrders(u.getPhone()));

        Flux<OrderInfo> orderInfos = orders.map(o -> OrderInfo.builder()
                .orderNumber(o.getOrderNumber())
                .userName(userId)
                .phoneNumber(o.getPhoneNumber())
                .productCode(o.getProductCode())
                .build());

        return user.flatMapMany(u -> orderInfos.flatMap(orderInfo -> {
            Mono<List<Product>> products = getProducts(orderInfo.getProductCode()).collectList();
            return products.map(l -> {
                int maxScore = 0;
                Product maxScoreProduct = null;
                for (Product p : l) {
                    if (p.getScore() > maxScore) {
                        maxScoreProduct = p;
                        maxScore++;
                    }
                }
                if (maxScoreProduct != null) {
                    orderInfo.setProductName(maxScoreProduct.getProductName());
                    orderInfo.setProductId(maxScoreProduct.getProductId());
                }
                return orderInfo;
            });
        }));
    }

    private Mono<Users> getUser(String userId) {
        return userInfoRepository.findById(userId)
                .onErrorResume(ex -> {
                    log.error("Error retrieving data from User Info Repository", ex);
                    return Mono.empty();
                }).doOnNext(u -> logWithRequestId("user retrieved from repository: " + u));
    }

    private Flux<Order> getOrders(String phoneNumber) {
        return externalServiceConnector.getOrdersFromExternalService(phoneNumber)
                .onErrorResume(ex -> {
                    log.error("Error retrieving data from Order Service", ex);
                    return Flux.empty();
                }).doOnNext(o -> logWithRequestId("order retrieved from Order Service" + o));
    }

    private Flux<Product> getProducts(String productCode) {
        return externalServiceConnector.getProductsFromExternalService(productCode)
                .onErrorResume(ex -> {
                    log.error("Error retrieving data from Product Service", ex);
                    return Flux.empty();
                }).doOnNext(p -> logWithRequestId("product retrieved from Product Service: " + p));
    }

    private void logWithRequestId(String message) {
        LogUtils.invokeWithPutRequestIdInMdc(requestId, () -> log.info(message));
    }
}
