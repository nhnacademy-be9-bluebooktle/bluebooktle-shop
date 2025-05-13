package shop.bluebooktle.backend.coupon.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.entity.AbsoluteCoupon;
import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.backend.coupon.entity.RelativeCoupon;
import shop.bluebooktle.backend.coupon.repository.AbsoluteCouponRepository;
import shop.bluebooktle.backend.coupon.repository.CouponTypeRepository;
import shop.bluebooktle.backend.coupon.repository.RelativeCouponRepository;
import shop.bluebooktle.backend.coupon.service.CouponTypeService;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.exception.InvalidInputValueException;
import shop.bluebooktle.common.exception.coupon.CouponTypeNameAlreadyException;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponTypeServiceImpl implements CouponTypeService {

	private final CouponTypeRepository couponTypeRepository;
	private final AbsoluteCouponRepository absoluteCouponRepository;
	private final RelativeCouponRepository relativeCouponRepository;

	@Override
	public void registerCouponType(CouponTypeRegisterRequest request) { // 쿠폰 정책 등록
		//이름 중복 예외처리
		couponTypeRepository.findByName(request.getName())
			.ifPresent(couponType -> {
				throw new CouponTypeNameAlreadyException();
			});

		CouponType couponType = couponTypeRepository.save(
			CouponType.builder()
				.name(request.getName())
				.target(request.getTarget())
				.minimumPayment(request.getMinimumPayment())
				.build()
		);

		// absolute or relative
		if (request.getDiscountPrice() != null) {
			absoluteCouponRepository.save(
				AbsoluteCoupon.builder()
					.couponType(couponType)
					.discountPrice(request.getDiscountPrice())
					.build()
			);
		} else if (request.getDiscountPercent() != null) {
			relativeCouponRepository.save(
				RelativeCoupon.builder()
					.couponType(couponType)
					.discountPercent(request.getDiscountPercent())
					.maximumDiscountPrice(request.getMaximumDiscountPrice())
					.build()
			);
		} else {
			throw new InvalidInputValueException("절대값 또는 상대값 할인 정보 중 하나는 필수입니다.");
		}

	}

	//쿠폰 정책 전체 조회
	@Override
	@Transactional(readOnly = true)
	public Page<CouponTypeResponse> getAllCouponTypeList(Pageable pageable) {
		return couponTypeRepository.findAllByCouponType(pageable);
	}
}
