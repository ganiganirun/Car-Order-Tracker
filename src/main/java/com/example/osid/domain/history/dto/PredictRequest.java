package com.example.osid.domain.history.dto;

import java.util.List;

import com.example.osid.domain.order.entity.OrderOption;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PredictRequest {
	@JsonProperty("model_type")
	private String modelType;
	@JsonProperty("option_list")
	private List<Long> optionList;

	public PredictRequest(String modelType, List<OrderOption> orderOptions) {
		this.modelType = modelType;
		this.optionList = orderOptions.stream().map(orderOption -> orderOption.getOption().getId()).toList();
	}
}
