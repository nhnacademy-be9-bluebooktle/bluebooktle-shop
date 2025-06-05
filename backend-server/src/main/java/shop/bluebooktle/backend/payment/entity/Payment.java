package shop.bluebooktle.backend.payment.entity;

import java.math.BigDecimal;

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
import lombok.Setter;
import lombok.ToString;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"order", "paymentDetail"})
@SQLDelete(sql = "UPDATE payment SET deleted_at = CURRENT_TIMESTAMP WHERE payment_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@Setter
	private Order order;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_detail_id", nullable = false, unique = true)
	private PaymentDetail paymentDetail;

	@Column(name = "paid_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal paidAmount;

	@Builder
	public Payment(Order order, PaymentDetail paymentDetail, BigDecimal paidAmount) {
		this.order = order;
		this.paymentDetail = paymentDetail;
		this.paidAmount = paidAmount;
	}
}
