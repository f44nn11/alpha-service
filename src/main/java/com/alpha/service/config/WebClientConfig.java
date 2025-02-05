package com.alpha.service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/*
 * Created by: fkusu
 * Date: 1/4/2025
 */
@Configuration
public class WebClientConfig {
 @Bean
 public WebClient.Builder webClientBuilder() {
  return WebClient.builder();
 }
}
