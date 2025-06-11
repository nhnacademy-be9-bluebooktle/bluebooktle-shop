package shop.bluebooktle.backend.book_order.entity; // 사용자가 지정한 패키지 경로

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import lombok.Setter;
import lombok.ToString;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.common.entity.auth.User;

@Entity
@Table(name = "user_coupon_book_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"userCoupon", "bookOrder", "order", "user"})

public class UserCouponBookOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_coupon_book_order_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_coupon_id", nullable = false)
	private UserCoupon userCoupon;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_order_id")
	private BookOrder bookOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public UserCouponBookOrder(UserCoupon userCoupon, BookOrder bookOrder, Order order, User user) {
		this.userCoupon = userCoupon;
		this.bookOrder = bookOrder;
		this.order = order;
		this.user = user;
	}
}