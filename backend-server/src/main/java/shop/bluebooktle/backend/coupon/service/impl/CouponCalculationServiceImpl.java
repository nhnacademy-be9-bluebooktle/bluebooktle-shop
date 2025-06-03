package shop.bluebooktle.backend.coupon.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.entity.UserCouponBookOrder;
import shop.bluebooktle.backend.coupon.entity.AbsoluteCoupon;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.backend.coupon.entity.RelativeCoupon;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.AbsoluteCouponRepository;
import shop.bluebooktle.backend.coupon.repository.RelativeCouponRepository;
import shop.bluebooktle.backend.coupon.service.CouponCalculationService;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.CalculatedDiscountDetails;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponCalculationServiceImpl implements CouponCalculationService {

	private final AbsoluteCouponRepository absoluteCouponRepository;
	private final RelativeCouponRepository relativeCouponRepository;

	@Override
	public CalculatedDiscountDetails calculateDiscountDetails(UserCouponBookOrder appliedCouponInfo) {
		if (appliedCouponInfo == null || appliedCouponInfo.getUserCoupon() == null) {
			return buildDefaultCalculatedDiscountDetails(BigDecimal.ZERO);
		}

		UserCoupon userCoupon = appliedCouponInfo.getUserCoupon();
		Coupon coupon = userCoupon.getCoupon();
		if (coupon == null || coupon.getCouponType() == null) {
			return buildDefaultCalculatedDiscountDetails(BigDecimal.ZERO);
		}

		CouponType couponType = coupon.getCouponType();
		BigDecimal baseAmountForDiscount = BigDecimal.ZERO;
		BigDecimal calculatedDiscount;
		String policyTypeDesc;
		BigDecimal originalDiscValue;
		BigDecimal maxDiscForPercentage = null;

		if (couponType.getTarget() == CouponTypeTarget.BOOK) {
			BookOrder bookOrder = appliedCouponInfo.getBookOrder();
			if (bookOrder == null) {
				return buildDefaultCalculatedDiscountDetails(BigDecimal.ZERO);
			}
			baseAmountForDiscount = bookOrder.getPrice().multiply(new BigDecimal(bookOrder.getQuantity()));
		} else if (couponType.getTarget() == CouponTypeTarget.ORDER) {
			Order order = appliedCouponInfo.getOrder();
			if (order == null || order.getBookOrders() == null || order.getBookOrders().isEmpty()) {
				return buildDefaultCalculatedDiscountDetails(BigDecimal.ZERO);
			}
			baseAmountForDiscount = order.getBookOrders().stream()
				.map(bo -> bo.getPrice().multiply(new BigDecimal(bo.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		}

		if (baseAmountForDiscount.compareTo(couponType.getMinimumPayment()) < 0) {
			return buildDefaultCalculatedDiscountDetails(BigDecimal.ZERO); // 최소 결제 금액 미달 시 할인 없음
		}

		Long couponTypeId = couponType.getId();
		Optional<AbsoluteCoupon> optAbsoluteCoupon = absoluteCouponRepository.findByCouponTypeId(couponTypeId);

		if (optAbsoluteCoupon.isPresent()) {
			AbsoluteCoupon absoluteCoupon = optAbsoluteCoupon.get();
			calculatedDiscount = absoluteCoupon.getDiscountPrice();
			policyTypeDesc = "정액 할인";
			originalDiscValue = absoluteCoupon.getDiscountPrice();
		} else {
			Optional<RelativeCoupon> optRelativeCoupon = relativeCouponRepository.findByCouponTypeId(couponTypeId);
			if (optRelativeCoupon.isPresent()) {
				RelativeCoupon relativeCoupon = optRelativeCoupon.get();
				policyTypeDesc = "정률 할인 (" + relativeCoupon.getDiscountPercent() + "%)";
				originalDiscValue = new BigDecimal(relativeCoupon.getDiscountPercent());
				maxDiscForPercentage = relativeCoupon.getMaximumDiscountPrice();

				BigDecimal discountPercentage = new BigDecimal(relativeCoupon.getDiscountPercent()).divide(
					new BigDecimal(100), 4, RoundingMode.HALF_UP);
				calculatedDiscount = baseAmountForDiscount.multiply(discountPercentage);
				if (calculatedDiscount.compareTo(relativeCoupon.getMaximumDiscountPrice()) > 0) {
					calculatedDiscount = relativeCoupon.getMaximumDiscountPrice();
				}
			} else {
				return buildDefaultCalculatedDiscountDetails(BigDecimal.ZERO); // 정책 정보 없음
			}
		}

		if (calculatedDiscount.compareTo(baseAmountForDiscount) > 0) {
			calculatedDiscount = baseAmountForDiscount;
		}
		if (calculatedDiscount.compareTo(BigDecimal.ZERO) < 0) {
			calculatedDiscount = BigDecimal.ZERO;
		}

		BigDecimal finalAppliedDiscount = calculatedDiscount.setScale(0, RoundingMode.DOWN);

		return CalculatedDiscountDetails.builder()
			.appliedDiscountAmount(finalAppliedDiscount)
			.policyTypeDescription(policyTypeDesc)
			.originalDiscountValue(originalDiscValue)
			.maxDiscountAmountForPercentage(maxDiscForPercentage)
			.build();
	}

	private CalculatedDiscountDetails buildDefaultCalculatedDiscountDetails(BigDecimal appliedAmount) {
		return CalculatedDiscountDetails.builder()
			.appliedDiscountAmount(appliedAmount)
			.policyTypeDescription("정보 없음")
			.originalDiscountValue(null)
			.maxDiscountAmountForPercentage(null)
			.build();
	}

}