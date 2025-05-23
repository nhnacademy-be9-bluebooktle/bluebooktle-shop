package shop.bluebooktle.backend.point.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.bluebooktle.common.domain.point.PolicyType;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "point_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE point_policy SET deleted_at = CURRENT_TIMESTAMP WHERE point_policy_id = ?")
@SQLRestriction("deleted_at IS NULL")
@ToString(exclude = {"pointSourceType"})
public class PointPolicy extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_policy_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "point_source_type_id", nullable = false, unique = true)
	private PointSourceType pointSourceType;

	@Enumerated(EnumType.STRING)
	@Column(name = "policy_type", nullable = false, length = 20)
	private PolicyType policyType;

	@Column(name = "value", nullable = false, precision = 10, scale = 2)
	private BigDecimal value;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = Boolean.FALSE;

	@Builder
	public PointPolicy(PointSourceType pointSourceType, PolicyType policyType, BigDecimal value) {
		this.pointSourceType = pointSourceType;
		this.policyType = policyType;
		this.value = value;
	}

	public void changeIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public void changeValue(BigDecimal value) {
		this.value = value;
	}

	public void changePolicyType(PolicyType policyType) {
		this.policyType = policyType;
	}
}
