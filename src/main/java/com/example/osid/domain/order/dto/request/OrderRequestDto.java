package com.example.osid.domain.order.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import org.openapitools.jackson.nullable.JsonNullable;

import com.example.osid.domain.order.enums.OrderStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

		@NotNull(message = "{javax.validation.constraints.NotBlank.message}")
		private Long modelId;

		@NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
		private String address;
	}

	@Getter
	@AllArgsConstructor
	public static class Update {
		private JsonNullable<String> address = JsonNullable.undefined();

		private OrderStatus orderStatus;

		private JsonNullable<LocalDateTime> expectedDeliveryAt = JsonNullable.undefined();

		private JsonNullable<LocalDateTime> actualDeliveryAt = JsonNullable.undefined();

	}

}
