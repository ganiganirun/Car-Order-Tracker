package com.example.osid.domain.history.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.osid.domain.history.dto.PredictRequest;

import io.netty.channel.ChannelOption;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@RestController
public class MLApiController {

	HttpClient hc = HttpClient.create()
		.responseTimeout(Duration.ofSeconds(25))         // 서버 응답 25s 제한
		.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000); // TCP 연결 5s 제한

	WebClient webClient = WebClient.builder()
		.baseUrl("https://mlp-learning-test.onrender.com")
		.clientConnector(new ReactorClientHttpConnector(hc))
		.build();

	// private final WebClient webClient = WebClient.builder()
	// 	.baseUrl("https://mlp-learning-test.onrender.com/predict_all")
	// 	.build();

	@PostMapping("/api/my-ml-predict")
	public Mono<Map> predictWithMLApi(@RequestBody PredictRequest input) {
		return webClient.post()
			.uri("/predict_all")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(input)
			.retrieve()
			.bodyToMono(Map.class);

	}
}
