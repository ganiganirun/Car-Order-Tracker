package com.example.osid.domain.history.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class PredictResponse {

	private List<Stage> stages;

	@JsonProperty("total_duration_h")
	private BigDecimal totalDuration;

	@Getter
	@Setter
	public static class Stage {
		private Integer stage;
		@JsonProperty("pred_duration_h")
		private BigDecimal predDuration;
		@JsonProperty("total_with_delay_h")
		private BigDecimal totalWithDelay;
		@JsonProperty("transport_delay_h")
		private BigDecimal transportDelay;
	}
}
