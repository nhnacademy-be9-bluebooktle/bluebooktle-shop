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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Getter
@Table(name = "payment_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"paymentType"})
@SQLDelete(sql = "UPDATE payment_detail SET deleted_at = CURRENT_TIMESTAMP WHERE payment_detail_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class PaymentDetail extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_detail_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_type_id", nullable = false)
	private PaymentType paymentType;

	@Column(name = "`key`", length = 255)
	private String key;

	@Builder
	public PaymentDetail(PaymentType paymentType, String key) {
		this.paymentType = paymentType;
		this.key = key;
	}
}
