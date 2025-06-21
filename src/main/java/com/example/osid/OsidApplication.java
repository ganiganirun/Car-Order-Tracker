package com.example.osid;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableAspectJAutoProxy // AOP 사용을 위함
@EnableRabbit
@EnableCaching
@EnableScheduling
// @EnableBatchProcessing
public class OsidApplication {

	public static void main(String[] args) {
		SpringApplication.run(OsidApplication.class, args);
	}

}
