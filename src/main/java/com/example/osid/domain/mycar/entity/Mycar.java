package com.example.osid.domain.mycar.entity;

import java.time.LocalDateTime;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mycars",
	indexes = {@Index(name = "idx_user_deleted_created", columnList = "user_id, deleted_at")})
public class Mycar extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orders_id", nullable = false)
	private Orders orders;

	@Column
	private LocalDateTime deletedAt;

	public Mycar(Orders orders) {
		this.orders = orders;
		this.user = orders.getUser();
	}

	public void setDeletedAt() {
		this.deletedAt = LocalDateTime.now();
	}
}
