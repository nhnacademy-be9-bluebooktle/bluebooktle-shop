package shop.bluebooktle.backend.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "order_state")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class OrderState extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_state_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "state", nullable = false, length = 10)
	private State state;

	public enum State {
		PENDING,
		SHIPPING,
		COMPLETED,
		RETURNED,
		CANCELED
	}
}
