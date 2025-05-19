package shop.bluebooktle.backend.payment.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import shop.bluebooktle.common.domain.payment.PaymentStatus;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Getter
@Table(name = "payment_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"payment,paymentType"})
@SQLDelete(sql = "UPDATE payment_detail SET deleted_at = CURRENT_TIMESTAMP WHERE payment_detail_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class PaymentDetail extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_detail_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id", nullable = false, unique = true)
	private Payment payment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_type_id", nullable = false)
	private PaymentType paymentType;

	@Column(name = "payment_key", length = 255)
	private String paymentKey;

	@Column(name = "payment_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	@Column(name = "requested_at", nullable = false)
	private LocalDateTime requestedAt;

	@Column(name = "approved_at")
	private LocalDateTime approvedAt;

	@Builder
	public PaymentDetail(Payment payment, PaymentType paymentType, String paymentKey, PaymentStatus paymentStatus,
		LocalDateTime requestedAt,
		LocalDateTime approvedAt) {
		this.payment = payment;
		this.paymentType = paymentType;
		this.paymentKey = paymentKey;
		this.paymentStatus = paymentStatus;
		this.requestedAt = requestedAt;
		this.approvedAt = approvedAt;
	}
}
