package shop.bluebooktle.backend.coupon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.FailedCouponIssue;
import shop.bluebooktle.common.domain.coupon.CouponIssueStatus;

public interface FailedCouponIssueRepository
	extends JpaRepository<FailedCouponIssue, Long>, FailedCouponIssueQueryRepository {
	List<FailedCouponIssue> findAllByStatus(CouponIssueStatus status);
}
