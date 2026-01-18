package com.hotel_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HotelAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelAuthServiceApplication.class, args);
	}

}