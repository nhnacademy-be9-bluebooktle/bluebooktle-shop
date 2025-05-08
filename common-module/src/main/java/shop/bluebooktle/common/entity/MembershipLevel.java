package shop.bluebooktle.common.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"users"})
@Entity
@Table(name = "membership_level")
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE membership_level SET deleted_at = CURRENT_TIMESTAMP WHERE membership_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class MembershipLevel extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "membership_id")
	private Long id;

	@Column(name = "name", nullable = false, length = 10)
	private String name;

	@Column(name = "rate", nullable = false)
	private Integer rate;

	@Column(name = "min_net_spent", nullable = false, precision = 10, scale = 2)
	private BigDecimal minNetSpent;

	@Column(name = "max_net_spent", nullable = false, precision = 10, scale = 2)
	private BigDecimal maxNetSpent;

	@OneToMany(mappedBy = "membershipLevel", fetch = FetchType.LAZY)
	private List<User> users = new ArrayList<>();

	@Builder
	public MembershipLevel(String name, Integer rate, BigDecimal minNetSpent, BigDecimal maxNetSpent) {
		this.name = name;
		this.rate = rate;
		this.minNetSpent = minNetSpent;
		this.maxNetSpent = maxNetSpent;
	}
}