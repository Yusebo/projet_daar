package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan(basePackages = "com.example.demo.library.models")
@EnableJpaRepositories(basePackages = "com.example.demo.library")
public class ProjetDaarApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetDaarApplication.class, args);
	}

}
