package com.example.osid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OsidApplication {

	public static void main(String[] args) {
		SpringApplication.run(OsidApplication.class, args);
	}

}
