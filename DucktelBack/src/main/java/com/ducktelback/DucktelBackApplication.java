package com.ducktelback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.ducktelback")
@EnableJpaRepositories(basePackages = "com.ducktelback.repository")
public class DucktelBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(DucktelBackApplication.class, args);
	}

}
