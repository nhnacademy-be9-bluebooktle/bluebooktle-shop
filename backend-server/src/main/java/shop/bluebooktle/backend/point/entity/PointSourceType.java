package shop.bluebooktle.backend.point.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.bluebooktle.common.domain.point.ActionType;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "point_source_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE point_source_type SET deleted_at = CURRENT_TIMESTAMP WHERE point_source_type_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class PointSourceType extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_source_type_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "action_type", nullable = false, length = 4)
	private ActionType actionType;

	@Column(name = "source_type", nullable = false, length = 50)
	private String sourceType;

	@Builder
	public PointSourceType(ActionType actionType, String sourceType) {
		this.actionType = actionType;
		this.sourceType = sourceType;
	}
}