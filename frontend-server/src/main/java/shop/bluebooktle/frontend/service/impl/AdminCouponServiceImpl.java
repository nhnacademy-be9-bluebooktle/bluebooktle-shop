package shop.bluebooktle.frontend.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.repository.AdminCouponRepository;
import shop.bluebooktle.frontend.service.AdminCouponService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCouponServiceImpl implements AdminCouponService {
	private final AdminCouponRepository adminCouponRepository;

	@Override
	public void registerCouponType(CouponTypeRegisterRequest request) {
		JsendResponse<Void> response = adminCouponRepository.registerCouponType(request);
		checkResponse(response, ErrorCode.INVALID_INPUT_VALUE, "쿠폰 정책 등록 실패");
	}

	@Override
	public PaginationData<CouponTypeResponse> getAllCouponTypes() {
		JsendResponse<PaginationData<CouponTypeResponse>> response = adminCouponRepository.getCouponType();
		checkResponse(response, ErrorCode.INTERNAL_SERVER_ERROR, "쿠폰 정책 조회 실패");
		return response.data();
	}

	@Override
	public void registerCoupon(CouponRegisterRequest request) {
		JsendResponse<Void> response = adminCouponRepository.registerCoupon(request);
		checkResponse(response, ErrorCode.INVALID_INPUT_VALUE, "쿠폰 등록 실패");
	}

	@Override
	public PaginationData<CouponResponse> getAllCoupons() {
		JsendResponse<PaginationData<CouponResponse>> response = adminCouponRepository.getAllCoupons();
		checkResponse(response, ErrorCode.INTERNAL_SERVER_ERROR, "쿠폰 조회 실패");
		return response.data();
	}

	@Override
	public void issueCoupon(UserCouponRegisterRequest request) {
		JsendResponse<Void> response = adminCouponRepository.issueCoupon(request);
		checkResponse(response, ErrorCode.BAD_GATEWAY, "쿠폰 발급 실패");
	}

	private void checkResponse(JsendResponse<?> response, ErrorCode defaultCode, String fallbackMessage) {
		if (response == null) {
			throw new ApplicationException(ErrorCode.BAD_GATEWAY, fallbackMessage + ": 응답 없음");
		}
		if ("success".equals(response.status())) {
			log.warn("Coupon API fail : status = {}, code = {}, message = {}", response.status(), response.code(),
				response.message());
			ErrorCode errorCode = ErrorCode.findByStringCode(response.code());
			if (errorCode == null) {
				errorCode = defaultCode;
			}
			throw new ApplicationException(errorCode,
				response.message() != null ? response.message() : fallbackMessage);
		}
	}
}
