package shop.bluebooktle.frontend.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponUpdateRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.repository.AdminCouponRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCouponService {
	private final AdminCouponRepository adminCouponRepository;

	public void registerCouponType(CouponTypeRegisterRequest request) {
		JsendResponse<Void> response = adminCouponRepository.registerCouponType(request);
		checkResponse(response, ErrorCode.INVALID_INPUT_VALUE, "쿠폰 정책 등록 실패");
	}

	public List<CouponTypeResponse> getCouponTypes() {
		JsendResponse<List<CouponTypeResponse>> response = adminCouponRepository.getCouponType();
		checkResponse(response, ErrorCode.INTERNAL_SERVER_ERROR, "쿠폰 정책 조회 실패");
		return response.data();
	}

	public void registerCoupon(CouponRegisterRequest request) {
		JsendResponse<Void> response = adminCouponRepository.registerCoupon(request);
		checkResponse(response, ErrorCode.INVALID_INPUT_VALUE, "쿠폰 등록 실패");
	}

	public CouponResponse getCoupon(Long couponId) {
		JsendResponse<CouponResponse> response = adminCouponRepository.getCoupon(couponId);
		checkResponse(response, ErrorCode.K_COUPON_NOT_FOUND, "쿠폰 조회 실패");
		return response.data();
	}

	public void updateCoupon(Long couponId, CouponUpdateRequest request) {
		JsendResponse<Void> response = adminCouponRepository.updateCoupon(request, couponId);
		checkResponse(response, ErrorCode.INVALID_INPUT_VALUE, "쿠폰 수정 실패");
	}

	public void deleteCoupon(Long couponId) {
		JsendResponse<Void> response = adminCouponRepository.deleteCoupon(couponId);
		checkResponse(response, ErrorCode.K_COUPON_NOT_FOUND, "쿠폰 삭제 실패");
	}

	public void issueCoupon(Long couponId, UserCouponRegisterRequest request) {
		JsendResponse<Void> response = adminCouponRepository.issueCoupon(couponId, request);
		checkResponse(response, ErrorCode.BAD_GATEWAY, "쿠폰 발급 실패");
	}

	// -=---------- 공용
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
