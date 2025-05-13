package shop.bluebooktle.backend.coupon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.query.CouponQueryRepository;
import shop.bluebooktle.common.entity.auth.User;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>, CouponQueryRepository {
	List<UserCoupon> findByUser(User user);

}
