package shop.bluebooktle.backend.coupon.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.entity.FailedCouponIssue;
import shop.bluebooktle.backend.coupon.mq.failure.FailedCouponIssuePublisher;
import shop.bluebooktle.backend.coupon.repository.FailedCouponIssueRepository;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.coupon.service.FailedCouponIssueService;
import shop.bluebooktle.common.domain.coupon.CouponIssueStatus;
import shop.bluebooktle.common.dto.coupon.request.FailedCouponIssueSearchRequest;
import shop.bluebooktle.common.dto.coupon.response.FailedCouponIssueResponse;
import shop.bluebooktle.common.exception.coupon.FailedCouponNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FailedCouponIssueServiceImpl implements FailedCouponIssueService {

	private final FailedCouponIssueRepository failedCouponIssueRepository;
	private final FailedCouponIssuePublisher publisher;
	private final UserCouponRepository userCouponRepository;

	@Override
	@Transactional
	public void resend(Long issueId) {
		FailedCouponIssue issue = failedCouponIssueRepository.findById(issueId)
			.orElseThrow(FailedCouponNotFoundException::new);

		if (issue.getStatus() == CouponIssueStatus.SUCCESS) {
			log.warn("실패 쿠폰 :재전송 스킵 이미 성공 처리된 건입니다. id={}", issueId);
			return;
		}

		try {
			publisher.resend(issue);
			issue.increaseRetryCount();
			issue.updateStatus(CouponIssueStatus.RETRIED);
			log.info("실패 쿠폰 : 단일 재전송 성공 id={}, userId={}, couponId={}", issue.getId(), issue.getUserId(),
				issue.getCouponId());
		} catch (Exception e) {
			issue.increaseRetryCount();
			log.error("실패 쿠폰 : 단일 재전송 실패 id={}, reason={}", issue.getId(), e.getMessage());
		}
	}

	@Override
	@Transactional
	public void resendAll() {
		List<FailedCouponIssue> failedIssues = failedCouponIssueRepository.findAllByStatus(CouponIssueStatus.FAILED);

		for (FailedCouponIssue issue : failedIssues) {
			try {
				resend(issue.getId());
			} catch (Exception e) {
				log.warn("실패 쿠폰 : 전체 재발송 실패 - issueId: {}", issue.getId(), e);
				issue.increaseRetryCount();
			}
		}
	}

	@Override
	public Page<FailedCouponIssueResponse> getAllFailedCoupons(
		FailedCouponIssueSearchRequest req, Pageable pageable) {

		Page<FailedCouponIssueResponse> page = failedCouponIssueRepository.findAllFailedCouponIssue(req, pageable);

		List<FailedCouponIssueResponse> adjusted = page.getContent().stream()
			.map(resp -> {
				boolean exists = userCouponRepository.existsByUserIdAndCouponIdAndAvailableStartAt(
					resp.getUserId(), resp.getCouponId(), resp.getAvailableStartAt()
				);

				if (exists && resp.getStatus() != CouponIssueStatus.SUCCESS) {
					return FailedCouponIssueResponse.builder()
						.id(resp.getId())
						.issueId(resp.getIssueId())
						.userId(resp.getUserId())
						.couponId(resp.getCouponId())
						.issueType(resp.getIssueType())
						.status(CouponIssueStatus.SUCCESS)
						.retryCount(resp.getRetryCount())
						.reason(resp.getReason())
						.availableStartAt(resp.getAvailableStartAt())
						.availableEndAt(resp.getAvailableEndAt())
						.createdAt(resp.getCreatedAt())
						.lastUpdatedAt(resp.getLastUpdatedAt())
						.build();
				}
				return resp;
			})
			.toList();

		return new PageImpl<>(adjusted, pageable, page.getTotalElements());
	}
}
