package com.example.github_demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApplicationConfig {
    @Bean
    public RestClient githubRestClient(@Value("${api.github.host}") String host) {
        return RestClient.builder()
                .baseUrl(host)
                .build();
    }
}
