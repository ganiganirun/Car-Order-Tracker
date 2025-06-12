package com.example.osid.config;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;

// JasonNullable 을 처리하는 방법을 알리기위해 추가적인 모듈을 등록
@Configuration
public class JacksonConfig {

	@Bean
	Jackson2ObjectMapperBuilder objectMapperBuilder() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.serializationInclusion(JsonInclude.Include.ALWAYS)
			.modulesToInstall(new JsonNullableModule());
		return builder;
	}
}
