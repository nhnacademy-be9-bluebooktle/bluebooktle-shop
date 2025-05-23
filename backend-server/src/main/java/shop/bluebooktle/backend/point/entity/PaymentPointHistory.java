package shop.bluebooktle.backend.point.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "payment_point_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE payment_point_history SET deleted_at = CURRENT_TIMESTAMP WHERE payment_point_history_id = ?")
@SQLRestriction("deleted_at IS NULL")
@ToString(exclude = {"payment", "pointHistory"})
public class PaymentPointHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_point_history_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id", nullable = false)
	private Payment payment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "point_id", nullable = false)
	private PointHistory pointHistory;

	@Builder
	public PaymentPointHistory(Payment payment, PointHistory pointHistory) {
		this.payment = payment;
		this.pointHistory = pointHistory;
	}
}
