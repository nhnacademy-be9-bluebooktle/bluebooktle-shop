package shop.bluebooktle.common.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"user"})
@Entity
@Table(name = "address")
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE address SET deleted_at = CURRENT_TIMESTAMP WHERE address_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Address extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "address_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "alias", nullable = false, length = 50)
	private String alias;

	@Column(name = "road_address", nullable = false)
	private String roadAddress;

	@Column(name = "detail_address", nullable = false)
	private String detailAddress;

	@Column(name = "postal_code", nullable = false, length = 5)
	private String postalCode;

	@Builder
	public Address(User user, String alias, String roadAddress, String detailAddress, String postalCode) {
		this.alias = alias;
		this.roadAddress = roadAddress;
		this.detailAddress = detailAddress;
		this.postalCode = postalCode;
	}

	void assignUser(User user) {
		this.user = user;
	}
}