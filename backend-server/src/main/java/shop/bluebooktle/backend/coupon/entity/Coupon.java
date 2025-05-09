package shop.bluebooktle.backend.coupon.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "coupon")
@SQLDelete(sql = "UPDATE coupon SET deleted_at = CURRENT_TIMESTAMP WHERE coupon_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"user_coupon", "coupon_type", "book_coupon", "category_coupon"})
public class Coupon extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_id", nullable = false)
	private Long id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "coupon_type_id", nullable = false)
	private CouponType couponType;
	@Column(name = "name", nullable = false, length = 100)
	private String couponName;
	@Column(name = "available_start_at", nullable = false)
	private LocalDateTime availableStartAt;
	@Column(name = "available_end_at", nullable = false)
	private LocalDateTime availableEndAt;

	@Builder
	public Coupon(CouponType type, String couponName, LocalDateTime availableStartAt, LocalDateTime availableEndAt) {
		this.couponType = type;
		this.couponName = couponName;
		this.availableStartAt = availableStartAt;
		this.availableEndAt = availableEndAt;
	}
}
