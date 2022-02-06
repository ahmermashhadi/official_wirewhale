package com.camunda.wirewhale;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication("camunda.showcase")
public class WirewhaleApplication {

	public static void main(String[] args) {
		SpringApplication.run(WirewhaleApplication.class, args);
	}

}
