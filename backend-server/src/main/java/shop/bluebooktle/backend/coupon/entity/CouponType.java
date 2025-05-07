package shop.bluebooktle.backend.coupon.entity;

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
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "coupon_type")
@SQLDelete(sql = "UPDATE coupon_type SET deleted_at = CURRENT_TIMESTAMP WHERE coupon_type_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class CouponType extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_type_id")
	private Long id;
	@Column(name = "name", nullable = false, length = 100, unique = true)
	private String name;
	@Enumerated(EnumType.STRING)
	@Column(name = "target", nullable = false, length = 5)
	private Target target;

	public enum Target {
		ORDER, BOOK
	}

	public CouponType(String name, Target target) {
		this.name = name;
		this.target = target;
	}
}
