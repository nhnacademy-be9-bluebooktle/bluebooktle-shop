package shop.bluebooktle.backend.order.entity;

import java.math.BigDecimal;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.bluebooktle.common.domain.RefundReason;
import shop.bluebooktle.common.domain.RefundStatus;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "refund")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"order"})
@SQLDelete(sql = "UPDATE refund SET deleted_at = CURRENT_TIMESTAMP WHERE return_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Refund extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refund_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false, unique = true)
	private Order order;

	@Column(name = "date", nullable = false)
	private LocalDateTime date;

	@Column(name = "reason", nullable = false)
	@Enumerated(EnumType.STRING)
	private RefundReason reason;

	@Column(name = "reason_detail", columnDefinition = "TEXT", nullable = false)
	private String reasonDetail;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private RefundStatus status;

	@Column(name = "price", nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Builder
	public Refund(Order order, LocalDateTime date, RefundReason reason, String reasonDetail,
		RefundStatus status, BigDecimal price) {
		this.order = order;
		this.date = date;
		this.reason = reason;
		this.reasonDetail = reasonDetail;
		this.status = status;
		this.price = price;
	}

	public void changeStatus(RefundStatus status) {
		this.status = status;
	}

}
