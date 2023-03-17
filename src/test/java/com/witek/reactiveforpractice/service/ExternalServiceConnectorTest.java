package com.witek.reactiveforpractice.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.witek.reactiveforpractice.model.Order;
import com.witek.reactiveforpractice.model.Product;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@RunWith(MockitoJUnitRunner.class)
public class ExternalServiceConnectorTest {

    private static final String HOST = "http://localhost:";
    private static final String PRODUCT_SERVICE_PATH = "/productInfoService/product/names/";
    private static final String ORDER_SERVICE_PATH = "/orderSearchService/order/phone/";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort());

    WebClient productServiceWebClient;
    WebClient orderServiceWebClient;
    ExternalServiceConnector externalServiceConnector;

    @Before
    public void onInit() {
        productServiceWebClient = WebClient.builder()
                .baseUrl(HOST + wireMockRule.port())
                .build();

        orderServiceWebClient = WebClient.builder()
                .baseUrl(HOST + wireMockRule.port())
                .build();

        externalServiceConnector = new ExternalServiceConnector(productServiceWebClient, orderServiceWebClient);
        ReflectionTestUtils.setField(externalServiceConnector, "productServicePath", PRODUCT_SERVICE_PATH);
        ReflectionTestUtils.setField(externalServiceConnector, "orderServicePath", ORDER_SERVICE_PATH);
    }

    @Test
    public void getOrdersFromExternalServiceTest() {
        //given
        stubFor(get(urlMatching(ORDER_SERVICE_PATH + ".*"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(RESPONSE_ORDER)));

        String phoneNo = "55-555";

        //when

        Flux<Order> orders = externalServiceConnector.getOrdersFromExternalService(phoneNo);

        //then
        StepVerifier.create(orders)
                .expectNext(new Order(phoneNo, "OrderX", "223344"))
                .verifyComplete();
    }

    @Test
    public void getProductsFromExternalServiceTest() {
        //given
        stubFor(get(urlMatching(PRODUCT_SERVICE_PATH + ".*"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(RESPONSE_PRODUCT)));

        String productCode = "XYZ";

        //when

        Flux<Product> orders = externalServiceConnector.getProductsFromExternalService(productCode);

        //then
        StepVerifier.create(orders)
                .expectNext(new Product("no1", productCode, "nail", 500))
                .verifyComplete();
    }

    @Test
    public void getOrdersFromExternalServiceTest_throwError() {
        //given
        stubFor(get(urlMatching(ORDER_SERVICE_PATH + ".*"))
                .willReturn(aResponse().withStatus(500)));

        String phoneNo = "55-555";

        //when

        Flux<Order> orders = externalServiceConnector.getOrdersFromExternalService(phoneNo);

        //then
        StepVerifier.create(orders)
                .verifyError();
    }

    @Test
    public void getProductsFromExternalServiceTest_throwError() {
        //given
        stubFor(get(urlMatching(PRODUCT_SERVICE_PATH + ".*"))
                .willReturn(aResponse().withStatus(500)));

        String productCode = "XYZ";

        //when

        Flux<Product> orders = externalServiceConnector.getProductsFromExternalService(productCode);

        //then
        StepVerifier.create(orders)
                .verifyError();
    }

    private static final String RESPONSE_ORDER = "{" +
            "    \"phoneNumber\": \"55-555\"," +
            "    \"orderNumber\": \"OrderX\"," +
            "    \"productCode\": \"223344\"" +
            "}";

    private static final String RESPONSE_PRODUCT = "{" +
            "    \"productId\": \"no1\"," +
            "    \"productCode\": \"XYZ\"," +
            "    \"productName\": \"nail\"," +
            "    \"score\": 500" +
            "}";
}