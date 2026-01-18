package com.hotel_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class HotelEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelEurekaServerApplication.class, args);
	}

}