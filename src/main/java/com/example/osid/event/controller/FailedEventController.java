package com.example.osid.event.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.osid.common.response.CommonResponse;
import com.example.osid.event.dto.FailedEventResponse;
import com.example.osid.event.service.FailedEventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/masters/failedEvent")
public class FailedEventController {

	private final FailedEventService failedEventService;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<Page<FailedEventResponse>> findAllFailedEvent(
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
	) {
		Page<FailedEventResponse> failedEvents = failedEventService.findAllFailedEvent(pageable);
		return CommonResponse.ok(failedEvents);
	}

	@PostMapping("/{failedEventId}")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<String> retry(
		@PathVariable Long failedEventId
	) {
		String retryResult = failedEventService.retryFailedEvent(failedEventId);

		return CommonResponse.ok(retryResult);
	}
}
