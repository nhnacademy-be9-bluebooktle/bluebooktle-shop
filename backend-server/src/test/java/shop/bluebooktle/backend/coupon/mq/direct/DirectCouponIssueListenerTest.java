package shop.bluebooktle.backend.coupon.mq.direct;

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
class DirectCouponIssueListenerTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private UserCouponRepository userCouponRepository;

	@InjectMocks
	private DirectCouponIssueListener listener;

	@Test
	@DisplayName("정상 발급 시 handleCouponIssue() 성공")
	void handleCouponIssue_success() {
		// given
		Long userId = 1L;
		Long couponId = 2L;
		LocalDateTime startAt = LocalDateTime.now();
		LocalDateTime endAt = startAt.plusDays(7);

		CouponIssueMessage message = new CouponIssueMessage(userId, couponId, startAt, endAt);

		User user = User.builder().id(userId).loginId("tester").build();
		Coupon coupon = Coupon.builder().id(couponId).couponName("테스트 쿠폰").build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
		when(userCouponRepository.save(any(UserCoupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		listener.handleCouponIssue(message);

		// then
		verify(userRepository).findById(userId);
		verify(couponRepository).findById(couponId);
		verify(userCouponRepository).save(any(UserCoupon.class));
	}

	@Test
	@DisplayName("존재하지 않는 유저일 경우 UserNotFoundException 발생")
	void handleCouponIssue_userNotFound() {
		// given
		Long userId = 1L;
		CouponIssueMessage message = new CouponIssueMessage(userId, 2L, LocalDateTime.now(),
			LocalDateTime.now().plusDays(1));
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> listener.handleCouponIssue(message))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("존재하지 않는 쿠폰일 경우 CouponNotFoundException 발생")
	void handleCouponIssue_couponNotFound() {
		// given
		Long userId = 1L;
		Long couponId = 2L;
		LocalDateTime startAt = LocalDateTime.now();
		LocalDateTime endAt = startAt.plusDays(7);

		CouponIssueMessage message = new CouponIssueMessage(userId, couponId, startAt, endAt);
		User user = User.builder().id(userId).loginId("tester").build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> listener.handleCouponIssue(message))
			.isInstanceOf(CouponNotFoundException.class);
	}
}