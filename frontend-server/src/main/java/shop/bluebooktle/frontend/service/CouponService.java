package shop.bluebooktle.frontend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.repository.CouponRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponService {

	public final CouponRepository couponRepository;

	// ----------- 사용자
	public List<UserCouponResponse> getAllCoupons() {
		JsendResponse<List<UserCouponResponse>> response = couponRepository.getAllCoupon();
		checkResponse(response, ErrorCode.INTERNAL_SERVER_ERROR, "전체 쿠폰 조회 실패");
		return response.data();
	}

	public List<UserCouponResponse> getUsableCoupons() {
		JsendResponse<List<UserCouponResponse>> response = couponRepository.getAllCouponUsable();
		checkResponse(response, ErrorCode.INTERNAL_SERVER_ERROR, "사용 가능한 쿠폰 조회 실패");
		return response.data();
	}

	public List<UserCouponResponse> getUsedCoupons() {
		JsendResponse<List<UserCouponResponse>> response = couponRepository.getAllCouponUsed();
		checkResponse(response, ErrorCode.INTERNAL_SERVER_ERROR, "사용 완료 쿠폰 조회 실패");
		return response.data();
	}

	public List<UserCouponResponse> getExpiredCoupons() {
		JsendResponse<List<UserCouponResponse>> response = couponRepository.getExpiredCoupon();
		checkResponse(response, ErrorCode.INTERNAL_SERVER_ERROR, "만료 쿠폰 조회 실패");
		return response.data();
	}

	public void deleteUserCoupon(Long userCouponId) {
		JsendResponse<Void> response = couponRepository.deleteUserCoupon(userCouponId);
		checkResponse(response, ErrorCode.K_USER_COUPON_NOT_FOUND, "유저 쿠폰 삭제 실패");
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
