package shop.bluebooktle.backend.payment.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Getter
@Table(name = "payment_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE payment_type SET deleted_at = CURRENT_TIMESTAMP WHERE payment_type_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class PaymentType extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_type_id")
	private Long id;

	@Column(name = "method", nullable = false, length = 50)
	private String method;

	@Builder
	public PaymentType(String method) {
		this.method = method;
	}
}
