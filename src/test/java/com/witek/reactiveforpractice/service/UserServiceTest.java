package com.witek.reactiveforpractice.service;

import com.witek.reactiveforpractice.model.Order;
import com.witek.reactiveforpractice.model.OrderInfo;
import com.witek.reactiveforpractice.model.Product;
import com.witek.reactiveforpractice.model.Users;
import com.witek.reactiveforpractice.repository.UserInfoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    UserInfoRepository userInfoRepository;

    @Mock
    ExternalServiceConnector externalServiceConnector;

    private UserService userService;

    @Before
    public void onInit() {
        userService = new UserService(userInfoRepository, externalServiceConnector);
    }

    @Test
    public void getOrderInfoTest_positiveScenario() {
        //given
        String userId = "user1";
        String name = "name";
        String phoneNo = "55-555";
        String orderNo = "123456";
        String productCode = "999";
        String productId = "22";
        String productName = "tap";

        //when
        Users user = new Users(userId, name, phoneNo);
        when(userInfoRepository.findById(userId)).thenReturn(Mono.just(user));
        Order order = new Order(user.getPhone(), orderNo, productCode);
        when(externalServiceConnector.getOrdersFromExternalService(phoneNo)).thenReturn(Flux.just(order));
        Product product = new Product(productId, productCode, productName, 55);
        when(externalServiceConnector.getProductsFromExternalService(order.getProductCode())).thenReturn(Flux.just(product));

        Flux<OrderInfo> orderInfo = userService.getOrderInfo(userId, "id-555");

        //then
        OrderInfo expected = new OrderInfo(orderNo, userId, phoneNo, productCode, productName, productId);
        StepVerifier.create(orderInfo)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    public void getOrderInfoTest_onOrderThrowError() {
        //given
        String userId = "user1";
        String name = "name";
        String phoneNo = "55-555";

        //when
        Users user = new Users(userId, name, phoneNo);
        when(userInfoRepository.findById(userId)).thenReturn(Mono.just(user));

        when(externalServiceConnector.getOrdersFromExternalService(phoneNo)).thenReturn(Flux.empty());
        Flux<OrderInfo> orderInfo = userService.getOrderInfo(userId, "id-555");

        //then
        StepVerifier.create(orderInfo)
                .expectNextCount(0)
                .verifyComplete();
    }
}