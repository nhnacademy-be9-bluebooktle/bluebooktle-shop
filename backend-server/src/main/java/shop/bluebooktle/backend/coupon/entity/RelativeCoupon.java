package shop.bluebooktle.backend.coupon.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "relative_coupon")
@SQLDelete(sql = "UPDATE relative_coupon SET deleted_at = CURRENT_TIMESTAMP WHERE coupon_type_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class RelativeCoupon extends BaseEntity {
	@Id
	@Column(name = "coupon_type_id")
	private Long id;
	@MapsId
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coupon_type_id")
	private CouponType couponType;
	@Column(name = "maximum_discount_price", precision = 10, scale = 2, nullable = false)
	private BigDecimal maximumDiscountPrice;
	@Column(name = "discount_percent", nullable = false)
	private int discountPercent;
}
