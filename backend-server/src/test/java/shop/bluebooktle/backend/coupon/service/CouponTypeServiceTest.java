package shop.bluebooktle.backend.coupon.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.backend.coupon.repository.AbsoluteCouponRepository;
import shop.bluebooktle.backend.coupon.repository.CouponTypeRepository;
import shop.bluebooktle.backend.coupon.repository.RelativeCouponRepository;
import shop.bluebooktle.backend.coupon.service.impl.CouponTypeServiceImpl;
import shop.bluebooktle.common.domain.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.exception.InvalidInputValueException;
import shop.bluebooktle.common.exception.coupon.CouponTypeNameAlreadyException;

@ExtendWith(MockitoExtension.class)
class CouponTypeServiceTest {

	@InjectMocks
	private CouponTypeServiceImpl couponTypeService;

	@Mock
	private CouponTypeRepository couponTypeRepository;
	@Mock
	private AbsoluteCouponRepository absoluteCouponRepository;
	@Mock
	private RelativeCouponRepository relativeCouponRepository;

	@Test
	@DisplayName("정액 쿠폰 정책 등록 - 성공")
	void registerCouponType_absolute_success() {
		CouponTypeRegisterRequest request = CouponTypeRegisterRequest.builder()
			.name("정액쿠폰")
			.target(CouponTypeTarget.ORDER)
			.minimumPayment(new BigDecimal("10000"))
			.discountPrice(new BigDecimal("1000"))
			.build();

		given(couponTypeRepository.findByName("정액쿠폰")).willReturn(Optional.empty());
		given(couponTypeRepository.save(any())).willAnswer(i -> i.getArguments()[0]);

		couponTypeService.registerCouponType(request);

		verify(couponTypeRepository).save(any());
		verify(absoluteCouponRepository).save(any());
	}

	@Test
	@DisplayName("정률 쿠폰 정책 등록 - 성공")
	void registerCouponType_relative_success() {
		CouponTypeRegisterRequest request = CouponTypeRegisterRequest.builder()
			.name("정률쿠폰")
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(new BigDecimal("20000"))
			.discountPercent(10)
			.maximumDiscountPrice(new BigDecimal("5000"))
			.build();

		given(couponTypeRepository.findByName("정률쿠폰")).willReturn(Optional.empty());
		given(couponTypeRepository.save(any())).willAnswer(i -> i.getArguments()[0]);

		couponTypeService.registerCouponType(request);

		verify(couponTypeRepository).save(any());
		verify(relativeCouponRepository).save(any());
	}

	@Test
	@DisplayName("이름 중복 - 실패")
	void registerCouponType_duplicateName_fail() {
		CouponTypeRegisterRequest request = CouponTypeRegisterRequest.builder()
			.name("중복쿠폰")
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(new BigDecimal("10000"))
			.discountPrice(new BigDecimal("1000"))
			.build();

		given(couponTypeRepository.findByName("중복쿠폰")).willReturn(Optional.of(mock(CouponType.class)));

		assertThatThrownBy(() -> couponTypeService.registerCouponType(request))
			.isInstanceOf(CouponTypeNameAlreadyException.class);
	}

	@Test
	@DisplayName("할인 정보 누락 등록 - 실패")
	void registerCouponType_noDiscountInfo_fail() {
		CouponTypeRegisterRequest request = CouponTypeRegisterRequest.builder()
			.name("할인없음")
			.target(CouponTypeTarget.ORDER)
			.minimumPayment(new BigDecimal("10000"))
			.build(); // 할인 필드 없음

		given(couponTypeRepository.findByName("할인없음")).willReturn(Optional.empty());
		given(couponTypeRepository.save(any())).willAnswer(i -> i.getArguments()[0]);

		assertThatThrownBy(() -> couponTypeService.registerCouponType(request))
			.isInstanceOf(InvalidInputValueException.class);
	}

	@Test
	@DisplayName("쿠폰 정책 전체 조회 - 성공")
	void getAllCouponTypeList_success() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<CouponTypeResponse> page = new PageImpl<>(List.of());
		given(couponTypeRepository.findAllByCouponType(pageable)).willReturn(page);

		Page<CouponTypeResponse> result = couponTypeService.getAllCouponTypeList(pageable);

		assertThat(result.getContent()).isInstanceOf(List.class);
		verify(couponTypeRepository).findAllByCouponType(pageable);
	}
}