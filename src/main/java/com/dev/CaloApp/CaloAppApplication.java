package com.dev.CaloApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Kích hoạt scheduler
public class CaloAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaloAppApplication.class, args);
	}

}
