package com.example.osid.domain.order.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OrderRequestDto {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Add {

		@NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
		private String userEmail;

		@NotEmpty(message = "{javax.validation.constraints.NotNull.message}")
		private List<Long> option;

		@NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
		private Long modelId;

		@NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
		private String address;
	}

}
