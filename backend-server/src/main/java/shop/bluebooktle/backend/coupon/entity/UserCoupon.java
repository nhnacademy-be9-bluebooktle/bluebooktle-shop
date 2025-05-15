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
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.coupon.UserCouponAlreadyUsedException;

@Entity
@Table(name = "user_coupon")
@SQLDelete(sql = "UPDATE user_coupon SET deleted_at = CURRENT_TIMESTAMP WHERE user_coupon_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"coupon", "user"})
public class UserCoupon extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_coupon_id")
	private Long id;
	@ManyToOne
	@JoinColumn(name = "coupon_id", nullable = false)
	private Coupon coupon;
	@Column(name = "used_at")
	private LocalDateTime usedAt;
	@Column(name = "available_start_at", nullable = false)
	private LocalDateTime availableStartAt;
	@Column(name = "available_end_at", nullable = false)
	private LocalDateTime availableEndAt;
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public UserCoupon(Coupon coupon, LocalDateTime usedAt, User user, LocalDateTime availableStartAt,
		LocalDateTime availableEndAt) {
		this.coupon = coupon;
		this.usedAt = usedAt;
		this.user = user;
		this.availableStartAt = availableStartAt;
		this.availableEndAt = availableEndAt;
	}

	public void useCoupon() {
		if (usedAt != null) {
			throw new UserCouponAlreadyUsedException();
		}
		this.usedAt = LocalDateTime.now();
	}
}
