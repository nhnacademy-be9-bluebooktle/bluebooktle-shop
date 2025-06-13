package shop.bluebooktle.backend.coupon.mq.birthday;

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

import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;

@ExtendWith(MockitoExtension.class)
class BirthdayCouponIssueListenerTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private UserCouponRepository userCouponRepository;

	@InjectMocks
	private BirthdayCouponIssueListener listener;

	@Test
	@DisplayName("생일 쿠폰 발급 성공")
	void birthdayCouponIssue_success() {
		// given
		Long userId = 1L;
		Long couponId = 2L;
		LocalDateTime startAt = LocalDateTime.now();
		LocalDateTime endAt = startAt.plusDays(5);
		CouponIssueMessage message = new CouponIssueMessage(userId, couponId, startAt, endAt);

		User user = User.builder().id(userId).loginId("user").build();
		Coupon coupon = Coupon.builder().id(couponId).couponName("생일쿠폰").build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
		when(userCouponRepository.save(any(UserCoupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		listener.birthdayCouponIssue(message);

		// then
		verify(userRepository).findById(userId);
		verify(couponRepository).findById(couponId);
		verify(userCouponRepository).save(any(UserCoupon.class));
	}

	@Test
	@DisplayName("유저 없음 - UserNotFoundException 발생")
	void birthdayCouponIssue_userNotFound() {
		// given
		Long userId = 99L;
		CouponIssueMessage message = new CouponIssueMessage(userId, 2L, LocalDateTime.now(),
			LocalDateTime.now().plusDays(1));

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> listener.birthdayCouponIssue(message))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("쿠폰 없음 - CouponNotFoundException 발생")
	void birthdayCouponIssue_couponNotFound() {
		// given
		Long userId = 1L;
		Long couponId = 99L;
		CouponIssueMessage message = new CouponIssueMessage(userId, couponId, LocalDateTime.now(),
			LocalDateTime.now().plusDays(1));
		User user = User.builder().id(userId).loginId("tester").build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> listener.birthdayCouponIssue(message))
			.isInstanceOf(CouponNotFoundException.class);
	}
}