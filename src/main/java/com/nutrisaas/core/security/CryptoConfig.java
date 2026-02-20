package com.nutrisaas.core.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfig {

    @Value("${crypto.key}")
    private String cryptoKey;

    @PostConstruct
    public void init() {
        System.out.println("----> init cryptoKey");
        CryptoUtil.init(cryptoKey);
    }
}