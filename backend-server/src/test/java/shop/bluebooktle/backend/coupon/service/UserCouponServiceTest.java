package shop.bluebooktle.backend.coupon.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.coupon.service.impl.UserCouponServiceImpl;
import shop.bluebooktle.backend.user.repository.MembershipLevelRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceTest {

	@InjectMocks
	private UserCouponServiceImpl userCouponService;

	@Mock
	private UserCouponRepository userCouponRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private CouponRepository couponRepository;
	@Mock
	private MembershipLevelRepository membershipLevelRepository;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	private User user;
	private Coupon coupon;
	private MembershipLevel membership;

	@BeforeEach
	void setup() {
		user = User.builder().build();
		coupon = Coupon.builder().build();
		ReflectionTestUtils.setField(user, "id", 1L);
		ReflectionTestUtils.setField(coupon, "id", 2L);

		membership = membershipLevelRepository.save(
			MembershipLevel.builder()
				.name("BASIC")
				.rate(1)
				.minNetSpent(BigDecimal.ZERO)
				.maxNetSpent(BigDecimal.valueOf(100000))
				.build()
		);
	}

	@Test
	@DisplayName("쿠폰 발급 - 성공")
	@WithMockUser
	void registerCoupon_success() {
		// given
		User user1 = User.builder()
			.loginId("user1")
			.name("유저1")
			.membershipLevel(membership)
			.email("user1@example.com")
			.nickname("유저1")
			.phoneNumber("01012345678")
			.birth("1990-01-01")
			.build();

		User user2 = User.builder()
			.loginId("user2")
			.name("유저2")
			.membershipLevel(membership)
			.nickname("유저2")
			.email("user2@example.com")
			.phoneNumber("01023456789")
			.birth("1991-01-01")
			.build();

		UserCouponRegisterRequest request = UserCouponRegisterRequest.builder()
			.couponId(2L)
			.availableStartAt(LocalDateTime.of(2025, 1, 1, 0, 0))
			.availableEndAt(LocalDateTime.of(2025, 12, 31, 0, 0))
			.build();

		given(couponRepository.findById(2L)).willReturn(Optional.of(coupon));
		given(userRepository.findByStatus(UserStatus.ACTIVE)).willReturn(List.of(user1, user2));

		// when
		userCouponService.registerCoupon(request);

		// then
		verify(userCouponRepository).saveAll(anyList());
	}

	@Test
	@DisplayName("쿠폰 발급 실패 - 쿠폰 없음")
	@WithMockUser
	void registerCoupon_couponNotFound() {
		UserCouponRegisterRequest request = UserCouponRegisterRequest.builder()
			.couponId(2L)
			.availableStartAt(LocalDateTime.now())
			.availableEndAt(LocalDateTime.now().plusDays(1))
			.build();

		given(couponRepository.findById(2L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> userCouponService.registerCoupon(request))
			.isInstanceOf(CouponNotFoundException.class);
	}
}

