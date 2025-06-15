package shop.bluebooktle.backend.coupon.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.entity.FailedCouponIssue;
import shop.bluebooktle.backend.coupon.entity.QFailedCouponIssue;
import shop.bluebooktle.backend.coupon.entity.QUserCoupon;
import shop.bluebooktle.backend.coupon.repository.FailedCouponIssueQueryRepository;
import shop.bluebooktle.common.domain.coupon.CouponIssueStatus;
import shop.bluebooktle.common.domain.coupon.CouponIssueType;
import shop.bluebooktle.common.dto.coupon.request.FailedCouponIssueSearchRequest;
import shop.bluebooktle.common.dto.coupon.response.FailedCouponIssueResponse;

@Slf4j
@RequiredArgsConstructor
public class FailedCouponIssueQueryRepositoryImpl implements FailedCouponIssueQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<FailedCouponIssueResponse> findAllFailedCouponIssue(FailedCouponIssueSearchRequest request,
		Pageable pageable) {

		QFailedCouponIssue failedCouponIssue = QFailedCouponIssue.failedCouponIssue;
		QUserCoupon userCoupon = QUserCoupon.userCoupon;

		BooleanExpression filterByType = eqIssueType(request.getType());
		BooleanExpression filterByStatus = eqStatus(request.getStatus());

		// RETRIED 상태면 실제로 발급 안된 것만 조회
		BooleanExpression notActuallyIssued = null;
		if (request.getStatus() == CouponIssueStatus.RETRIED) {
			notActuallyIssued = JPAExpressions
				.selectOne()
				.from(userCoupon)
				.where(
					userCoupon.user.id.eq(failedCouponIssue.userId),
					userCoupon.coupon.id.eq(failedCouponIssue.couponId),
					userCoupon.availableStartAt.eq(failedCouponIssue.availableStartAt)
				)
				.notExists();
		}

		List<FailedCouponIssue> content = queryFactory.selectFrom(failedCouponIssue)
			.where(
				filterByType,
				filterByStatus,
				notActuallyIssued
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(failedCouponIssue.id.asc())
			.fetch();

		Long total = queryFactory.select(failedCouponIssue.count())
			.from(failedCouponIssue)
			.where(
				filterByType,
				filterByStatus,
				notActuallyIssued
			)
			.fetchOne();

		List<FailedCouponIssueResponse> responses = content.stream()
			.map(issue -> FailedCouponIssueResponse.builder()
				.id(issue.getId())
				.issueId(issue.getIssueId())
				.userId(issue.getUserId())
				.couponId(issue.getCouponId())
				.issueType(issue.getIssueType())
				.status(issue.getStatus())
				.retryCount(issue.getRetryCount())
				.reason(issue.getReason())
				.availableStartAt(issue.getAvailableStartAt())
				.availableEndAt(issue.getAvailableEndAt())
				.createdAt(issue.getCreatedAt())
				.lastUpdatedAt(issue.getUpdatedAt())
				.build())
			.toList();

		return new PageImpl<>(responses, pageable, total != null ? total : 0);
	}

	private BooleanExpression eqIssueType(CouponIssueType issueType) {
		return issueType != null ? QFailedCouponIssue.failedCouponIssue.issueType.eq(issueType) : null;
	}

	private BooleanExpression eqStatus(CouponIssueStatus status) {
		return status != null ? QFailedCouponIssue.failedCouponIssue.status.eq(status) : null;
	}
}
