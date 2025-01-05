package com.alpha.booking_account.config;


import feign.form.spring.SpringFormEncoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.Encoder;

/*
 * Created by: fkusu
 * Date: 1/4/2025
 */
@Configuration
public class FeignConfiguration {
    @Bean
    public SpringFormEncoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(HttpMessageConverters::new));
    }
}
