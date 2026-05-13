package com.proyecto.scca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SccaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SccaApplication.class, args);
	}

}
