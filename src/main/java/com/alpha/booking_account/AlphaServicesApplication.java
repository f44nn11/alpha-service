package com.alpha.booking_account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AlphaServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlphaServicesApplication.class, args);
	}

}
