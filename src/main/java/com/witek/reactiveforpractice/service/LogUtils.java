package com.witek.reactiveforpractice.service;

import org.slf4j.MDC;

public class LogUtils {

    public static void invokeWithPutRequestIdInMdc(String requestId, Invokable method) {
        MDC.put("MDC_requestId", requestId);
        method.invoke();
    }

    public interface Invokable {
        void invoke();
    }
}