package com.witek.reactiveforpractice.controller;

import com.witek.reactiveforpractice.model.OrderInfo;
import com.witek.reactiveforpractice.service.LogUtils;
import com.witek.reactiveforpractice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping(value = "getOrdersInfo/{userId}")
    public Flux<OrderInfo> getOrdersInfo(@RequestHeader("requestId") String requestId, @PathVariable String userId) {

        LogUtils.invokeWithPutRequestIdInMdc(requestId, () -> log.info("Register new request: {}", requestId));
        return userService.getOrderInfo(userId, requestId);
    }
}