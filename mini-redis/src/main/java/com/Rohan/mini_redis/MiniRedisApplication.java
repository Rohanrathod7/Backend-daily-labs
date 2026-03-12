package com.Rohan.mini_redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MiniRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniRedisApplication.class, args);
	}

}
