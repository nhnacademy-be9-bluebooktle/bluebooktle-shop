package shop.bluebooktle.backend.coupon.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.coupon.service.impl.UserCouponServiceImpl;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.InvalidInputValueException;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;
import shop.bluebooktle.common.exception.coupon.UserCouponNotFoundException;

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

	private User user;
	private Coupon coupon;

	@BeforeEach
	void setup() {
		user = User.builder().build();
		coupon = Coupon.builder().build();
		ReflectionTestUtils.setField(user, "id", 1L);
		ReflectionTestUtils.setField(coupon, "id", 2L);
	}

	@Test
	@DisplayName("쿠폰 발급 - 성공")
		// TODO 유저 전체한테 보내도록 변경
	void registerCoupon_success() {
		UserCouponRegisterRequest request = UserCouponRegisterRequest.builder()
			.couponId(2L)
			.availableStartAt(LocalDateTime.of(2025, 1, 1, 0, 0))
			.availableEndAt(LocalDateTime.of(2025, 12, 31, 0, 0))
			.build();

		given(userRepository.findById(1L)).willReturn(Optional.of(user));
		given(couponRepository.findById(2L)).willReturn(Optional.of(coupon));

		userCouponService.registerCoupon(request);

		verify(userCouponRepository).save(any(UserCoupon.class));
	}

	@Test
	@DisplayName("쿠폰 발급 실패 - 사용자 없음")
	void registerCoupon_userNotFound() {
		UserCouponRegisterRequest request = UserCouponRegisterRequest.builder()
			.couponId(2L)
			.availableStartAt(LocalDateTime.now())
			.availableEndAt(LocalDateTime.now().plusDays(1))
			.build();

		given(userRepository.findById(1L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> userCouponService.registerCoupon(request))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("쿠폰 발급 실패 - 쿠폰 없음")
	void registerCoupon_couponNotFound() {
		UserCouponRegisterRequest request = UserCouponRegisterRequest.builder()
			.couponId(2L)
			.availableStartAt(LocalDateTime.now())
			.availableEndAt(LocalDateTime.now().plusDays(1))
			.build();

		given(userRepository.findById(1L)).willReturn(Optional.of(user));
		given(couponRepository.findById(2L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> userCouponService.registerCoupon(request))
			.isInstanceOf(CouponNotFoundException.class);
	}

	@Test
	@DisplayName("쿠폰 발급 실패 - 시작일 > 종료일")
	void registerCoupon_invalidDate_fail() {
		UserCouponRegisterRequest request = UserCouponRegisterRequest.builder()
			.couponId(2L)
			.availableStartAt(LocalDateTime.now().plusDays(5))
			.availableEndAt(LocalDateTime.now())
			.build();

		given(userRepository.findById(1L)).willReturn(Optional.of(user));
		given(couponRepository.findById(2L)).willReturn(Optional.of(coupon));

		assertThatThrownBy(() -> userCouponService.registerCoupon(request))
			.isInstanceOf(InvalidInputValueException.class);
	}

	@Test
	@DisplayName("유저 쿠폰 전체 조회 - 성공")
	void getAllUserCoupons_success() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<UserCouponResponse> page = new PageImpl<>(List.of());

		given(userCouponRepository.findAllByUserCoupon(user, pageable)).willReturn(page);

		Page<UserCouponResponse> result = userCouponService.getAllUserCoupons(user, pageable);

		assertThat(result.getContent()).isInstanceOf(List.class);
		verify(userCouponRepository).findAllByUserCoupon(user, pageable);
	}

	@Test
	@DisplayName("유저 사용 가능 쿠폰 조회 - 성공")
	void getAvailableUserCoupons_success() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<UserCouponResponse> page = new PageImpl<>(List.of());

		given(userCouponRepository.findAllByAvailableUserCoupon(user, pageable)).willReturn(page);

		Page<UserCouponResponse> result = userCouponService.getAvailableUserCoupons(user, pageable);

		assertThat(result.getContent()).isInstanceOf(List.class);
		verify(userCouponRepository).findAllByAvailableUserCoupon(user, pageable);
	}

	@Test
	@DisplayName("쿠폰 사용 - 성공")
	void useCoupon_success() {
		UserCoupon userCoupon = mock(UserCoupon.class);
		given(userCouponRepository.findById(1L)).willReturn(Optional.of(userCoupon));

		userCouponService.useCoupon(1L);

		verify(userCoupon).useCoupon();
	}

	@Test
	@DisplayName("쿠폰 사용 - 존재하지 않음")
	void useCoupon_notFound() {
		given(userCouponRepository.findById(1L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> userCouponService.useCoupon(1L))
			.isInstanceOf(UserCouponNotFoundException.class);
	}

	@Test
	@DisplayName("쿠폰 삭제 - 성공")
	void deleteCoupon_success() {
		UserCoupon userCoupon = mock(UserCoupon.class);
		given(userCouponRepository.findById(1L)).willReturn(Optional.of(userCoupon));

		userCouponService.deleteCoupon(1L);

		verify(userCouponRepository).delete(userCoupon);
	}

	@Test
	@DisplayName("쿠폰 삭제 - 존재하지 않음")
	void deleteCoupon_notFound() {
		given(userCouponRepository.findById(1L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> userCouponService.deleteCoupon(1L))
			.isInstanceOf(UserCouponNotFoundException.class);
	}
}

