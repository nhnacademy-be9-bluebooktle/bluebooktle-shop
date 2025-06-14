package shop.bluebooktle.backend.coupon.mq.welcome;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.coupon.request.WelcomeCouponIssueMessage;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;

@ExtendWith(MockitoExtension.class)
class WelcomeCouponListenerTest {

	@Mock
	private UserCouponRepository userCouponRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CouponRepository couponRepository;

	@InjectMocks
	private WelcomeCouponListener listener;

	@Test
	@DisplayName("웰컴 쿠폰 발급 성공")
	void handleWelcomeCoupon_success() {
		// given
		Long userId = 1L;
		Long couponId = 2L;
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusDays(7);

		WelcomeCouponIssueMessage message = new WelcomeCouponIssueMessage(userId, couponId, start, end);

		User user = User.builder().id(userId).loginId("testuser").build();
		Coupon coupon = Coupon.builder().id(couponId).couponName("WelcomeCoupon").build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
		when(userCouponRepository.save(any(UserCoupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		listener.handleWelcomeCoupon(message);

		// then
		verify(userRepository).findById(userId);
		verify(couponRepository).findById(couponId);
		verify(userCouponRepository).save(any(UserCoupon.class));
	}

	@Test
	@DisplayName("유저 없음 - UserNotFoundException 발생")
	void handleWelcomeCoupon_userNotFound() {
		// given
		Long userId = 99L;
		WelcomeCouponIssueMessage message = new WelcomeCouponIssueMessage(userId, 1L, LocalDateTime.now(),
			LocalDateTime.now().plusDays(1));
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> listener.handleWelcomeCoupon(message))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("쿠폰 없음 - CouponNotFoundException 발생")
	void handleWelcomeCoupon_couponNotFound() {
		// given
		Long userId = 1L;
		Long couponId = 99L;
		WelcomeCouponIssueMessage message = new WelcomeCouponIssueMessage(userId, couponId, LocalDateTime.now(),
			LocalDateTime.now().plusDays(1));
		User user = User.builder().id(userId).loginId("tester").build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> listener.handleWelcomeCoupon(message))
			.isInstanceOf(CouponNotFoundException.class);
	}
}
