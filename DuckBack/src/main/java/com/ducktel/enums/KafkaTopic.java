package com.ducktel.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum KafkaTopic {
    PAYMENT("payment-topic");

    private final String topic;
}
