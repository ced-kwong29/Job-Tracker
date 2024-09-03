package com.cedrickwong.backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ParseurAPIConfig {

    @Value("${parseur.api.base-url}")
    private String baseURL;

    @Value("${parseur.api.key}")
    private String apiKey;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(baseURL)
                                    .defaultHeader("Authorization", "Token " + apiKey)
                                    .build();
    }

    @Bean
    public ParseurClient parseurClient(WebClient webClient) {
        return new ParseurClient(webClient);
    }
}
