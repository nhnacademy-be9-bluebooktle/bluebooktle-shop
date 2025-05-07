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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.bluebooktle.common.entity.BaseEntity;

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
	@Column(name = "used_at", nullable = false)
	private LocalDateTime usedAt;

	//TODO User Entity 생성 시 주석 제거
	// @ManyToOne
	// @JoinColumn(name = "user_id", nullable = false)
	// private User user;
}
