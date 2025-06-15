package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.FailedCouponIssueSearchRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.dto.coupon.response.FailedCouponIssueResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(url = "${server.gateway-url}", path = "/api/admin/coupons", name = "adminCouponRepository", configuration = FeignGlobalConfig.class)
public interface AdminCouponRepository {
	//쿠폰 정책 등록
	@PostMapping("/type")
	@RetryWithTokenRefresh
	void registerCouponType(@RequestBody CouponTypeRegisterRequest request);

	//쿠폰 정책 전체 조회
	@GetMapping("/type")
	@RetryWithTokenRefresh
	PaginationData<CouponTypeResponse> getAllCouponType(
		@RequestParam("page") int page,
		@RequestParam("size") int size
	);

	//쿠폰 등록
	@PostMapping
	@RetryWithTokenRefresh
	void registerCoupon(@RequestBody CouponRegisterRequest request);

	//전체 쿠폰 조회
	@GetMapping
	@RetryWithTokenRefresh
	PaginationData<CouponResponse> getAllCoupon(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchCouponName", required = false) String searchCouponName
	);

	//쿠폰 발급
	@PostMapping("/issue")
	@RetryWithTokenRefresh
	void issueCoupon(@RequestBody UserCouponRegisterRequest request);

	// 실패한 쿠폰 목록 조회
	@GetMapping("/failed")
	PaginationData<FailedCouponIssueResponse> getAllFailedCouponIssue(
		@SpringQueryMap FailedCouponIssueSearchRequest request,
		@RequestParam("page") int page,
		@RequestParam("size") int size
	);

	// 단일 실패 쿠폰 재전송
	@PostMapping("/failed/{issueId}/resend")
	@RetryWithTokenRefresh
	void resendFailedCoupon(@PathVariable("issueId") Long issueId);

	// 전체 재발급
	@PostMapping("/failed/resend-all")
	@RetryWithTokenRefresh
	void resendAllFailedCoupons();
}
