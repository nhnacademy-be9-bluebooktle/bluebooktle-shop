package shop.bluebooktle.common.entity.point;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.bluebooktle.common.converter.PointSourceTypeConverter;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.entity.BaseEntity;
import shop.bluebooktle.common.entity.auth.User;

@Entity
@Table(name = "point_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE point_history SET deleted_at = CURRENT_TIMESTAMP WHERE point_id = ?")
@SQLRestriction("deleted_at IS NULL")
@ToString(exclude = "user")
public class PointHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_id")
	private Long id;

	@Convert(converter = PointSourceTypeConverter.class)
	@Column(name = "point_source_type_id", nullable = false)
	private PointSourceTypeEnum sourceType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "value", nullable = false, precision = 10, scale = 2)
	private BigDecimal value;

	@Builder
	public PointHistory(PointSourceTypeEnum sourceType, User user, BigDecimal value) {
		this.sourceType = sourceType;
		this.user = user;
		this.value = value;
	}
}
