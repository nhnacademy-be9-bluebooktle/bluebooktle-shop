package shop.bluebooktle.backend.coupon.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.bluebooktle.common.domain.coupon.CouponIssueStatus;
import shop.bluebooktle.common.domain.coupon.CouponIssueType;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "failed_user_coupon")
@SQLDelete(sql = "UPDATE failed_user_coupon SET deleted_at = CURRENT_TIMESTAMP WHERE failed_coupon_issue_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
public class FailedCouponIssue extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "failed_coupon_issue_id", nullable = false)
	private Long id;

	@Column(name = "issue_id", nullable = false, length = 36, unique = true)
	private String issueId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "coupon_id", nullable = false)
	private Long couponId;

	@Column(name = "available_start_at", nullable = false)
	private LocalDateTime availableStartAt;

	@Column(name = "available_end_at", nullable = false)
	private LocalDateTime availableEndAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "issue_type", nullable = false, length = 20)
	private CouponIssueType issueType;

	@Column(name = "retry_count", nullable = false)
	private int retryCount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private CouponIssueStatus status;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "reason", length = 50)
	private String reason;

	@PrePersist
	public void prePersist() {
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public void increaseRetryCount() {
		this.retryCount++;
	}

	public void updateStatus(CouponIssueStatus newStatus) {
		this.status = newStatus;
	}
}
