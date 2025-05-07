package shop.bluebooktle.backend.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "refund")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"order"})
public class Refund extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "return_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false, unique = true)
	private Order order;

	@Column(name = "date", nullable = false)
	private LocalDateTime date;

	@Column(name = "reason", nullable = false)
	private Reason reason;

	//TODO @Lob이 mysql에서 TEXT type인지 확인
	@Lob
	@Column(name = "reason_detail", nullable = false)
	private String reasonDetail;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;

	@Column(name = "price", nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	public enum Status {
		PENDING,
		PROGRESS,
		COMPLETE,
		CANCELED
	}

	public enum Reason {
		CHANGE_OF_MIND,
		DEFECT,
		DAMAGED,
		WRONG_DELIVERY,
		OTHER
	}
}
