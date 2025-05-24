package shop.bluebooktle.backend.order.entity;

import java.math.BigDecimal;

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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.domain.order.Region;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "delivery_rule")
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE delivery_rule SET deleted_at = CURRENT_TIMESTAMP WHERE delivery_rule_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class DeliveryRule extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "delivery_rule_id")
	private Long id;

	@Column(name = "rule_name", nullable = false, length = 50)
	private String ruleName;

	@Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
	private BigDecimal deliveryFee;

	@Column(name = "free_delivery_threshold", precision = 10, scale = 2)
	private BigDecimal freeDeliveryThreshold;

	@Enumerated(EnumType.STRING)
	@Column(name = "region", nullable = false, unique = true, length = 20)
	private Region region;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@Builder
	public DeliveryRule(
		String ruleName,
		BigDecimal deliveryFee,
		BigDecimal freeDeliveryThreshold,
		Region region,
		Boolean isActive
	) {
		this.ruleName = ruleName;
		this.deliveryFee = deliveryFee;
		this.freeDeliveryThreshold = freeDeliveryThreshold;
		this.region = region;
		this.isActive = isActive;
	}
}
