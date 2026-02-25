package com.goTyolo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GoTyoloApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoTyoloApplication.class, args);
	}

}
