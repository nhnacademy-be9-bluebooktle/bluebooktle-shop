package shop.bluebooktle.backend.payment.entity;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.bluebooktle.common.entity.User;

@Entity
@Table(name = "payment_point_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"payment", "pointHistory", "user"})
@SQLDelete(sql = "UPDATE payment_point_history SET deleted_at = CURRENT_TIMESTAMP WHERE payment_point_history_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class PaymentPointHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_point_history_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id", nullable = false)
	private Payment payment;

	// @OneToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "point_id", nullable = false,unique = true)
	// private PointHistory pointHistory;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Builder
	public PaymentPointHistory(Payment payment, User user) {
		this.payment = payment;
		this.user = user;
	}
}
