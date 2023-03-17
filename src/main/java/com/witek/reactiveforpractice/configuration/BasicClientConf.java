package com.witek.reactiveforpractice.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Configuration
public class BasicClientConf {

    @Value("${productService.baseUrl}")
    private String productServiceBaseUrl;

    @Value("${orderService.baseUrl}")
    private String orderServiceBaseUrl;

    @Value("${productService.timeout}")
    private int productServiceTimeoutSeconds;

    @Value("${orderService.timeout}")
    private int orderServiceTimeoutSeconds;

    @Bean
    public WebClient productServiceWebClient() {
        return getTimeoutClient(productServiceBaseUrl, productServiceTimeoutSeconds);
    }

    @Bean
    public WebClient orderServiceWebClient() {
        return getTimeoutClient(orderServiceBaseUrl, orderServiceTimeoutSeconds);
    }

    private WebClient getTimeoutClient(String baseUrl, int timeoutSeconds) {
        final TcpClient timeoutClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutSeconds * 1000)
                .doOnConnected(c -> c.addHandlerLast(new ReadTimeoutHandler(timeoutSeconds))
                        .addHandlerLast(new WriteTimeoutHandler(timeoutSeconds)));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(timeoutClient)))
                .build();
    }
}
