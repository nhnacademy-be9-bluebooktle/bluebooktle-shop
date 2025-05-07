package shop.bluebooktle.backend.order.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
import shop.bluebooktle.common.domain.OrderStatus;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "order_state")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE order_state SET deleted_at = CURRENT_TIMESTAMP WHERE order_state_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class OrderState extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_state_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "state", nullable = false, length = 10)
	private OrderStatus state;

}
