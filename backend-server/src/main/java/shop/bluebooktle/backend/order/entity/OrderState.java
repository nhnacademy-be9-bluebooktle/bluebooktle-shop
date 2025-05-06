package shop.bluebooktle.backend.order.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_state")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class OrderState {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_state_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "state", nullable = false)
	private State state;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public enum State {
		PENDING,
		SHIPPING,
		COMPLETED,
		RETURNED,
		CANCELED
	}
}
