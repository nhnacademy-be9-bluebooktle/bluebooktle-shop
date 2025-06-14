package shop.bluebooktle.frontend.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.frontend.repository.AdminCouponRepository;
import shop.bluebooktle.frontend.service.impl.AdminCouponServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AdminCouponServiceTest {

	@Mock
	AdminCouponRepository adminCouponRepository;

	@InjectMocks
	AdminCouponServiceImpl adminCouponService;

	@Test
	@DisplayName("쿠폰 정책 등록")
	void registerCouponType() {
		//given
		CouponTypeRegisterRequest request = CouponTypeRegisterRequest.builder()
			.name("test 쿠폰 정책")
			.target(CouponTypeTarget.ORDER)
			.minimumPayment(BigDecimal.valueOf(10000))
			.discountPrice(BigDecimal.valueOf(1000))
			.build();

		//when
		adminCouponService.registerCouponType(request);

		//then
		verify(adminCouponRepository).registerCouponType(request);
	}

	@Test
	@DisplayName("전체 쿠폰 정책 조회")
	void getAllCouponType() {
		Pageable pageable = PageRequest.of(0, 10);
		List<CouponTypeResponse> content = List.of(new CouponTypeResponse());
		Page<CouponTypeResponse> page = new PageImpl<>(content, pageable, content.size());
		PaginationData<CouponTypeResponse> response = new PaginationData<>(page);

		when(adminCouponRepository.getAllCouponType(0, 10)).thenReturn(response);

		//when
		PaginationData<CouponTypeResponse> result = adminCouponService.getAllCouponType(pageable);

		//then
		assertThat(result.getContent()).hasSize(1);
		verify(adminCouponRepository).getAllCouponType(0, 10);
	}

	@Test
	@DisplayName("쿠폰 등록")
	void registerCoupon() {
		// given
		CouponRegisterRequest request = CouponRegisterRequest.builder()
			.couponTypeId(1L)
			.name("신규 가입 쿠폰")
			.bookId(null)
			.categoryId(null)
			.build();

		// when
		adminCouponService.registerCoupon(request);

		// then
		verify(adminCouponRepository).registerCoupon(request);
	}

	@Test
	@DisplayName("전체 쿠폰 조회")
	void getAllCoupon() {
		// given
		Pageable pageable = PageRequest.of(0, 5);
		String searchCouponName = "신규";
		List<CouponResponse> content = List.of(CouponResponse.builder().build());
		Page<CouponResponse> page = new PageImpl<>(content, pageable, content.size());
		PaginationData<CouponResponse> response = new PaginationData<>(page);

		when(adminCouponRepository.getAllCoupon(0, 5, searchCouponName)).thenReturn(response);

		// when
		PaginationData<CouponResponse> result = adminCouponService.getAllCoupon(pageable, searchCouponName);

		// then
		assertThat(result.getContent()).hasSize(1);
		verify(adminCouponRepository).getAllCoupon(0, 5, searchCouponName);
	}

	@Test
	@DisplayName("쿠폰 발급")
	void issueCoupon() {
		// given
		UserCouponRegisterRequest request = UserCouponRegisterRequest.builder()
			.couponId(1L)
			.availableStartAt(LocalDateTime.now())
			.availableEndAt(LocalDateTime.now().plusDays(7))
			.build();

		// when
		adminCouponService.issueCoupon(request);

		// then
		verify(adminCouponRepository).issueCoupon(request);
	}
}
