package shop.bluebooktle.common.entity.auth;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"addresses", "membershipLevel"})
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE `user` SET deleted_at = CURRENT_TIMESTAMP WHERE user_id = ?")
@SQLRestriction("deleted_at IS NULL AND status <> 'WITHDRAWN'")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "membership_id", nullable = false)
	private MembershipLevel membershipLevel;

	@Column(name = "login_id", nullable = false, unique = true, length = 50)
	private String loginId;

	@Column(name = "password", nullable = false, length = 255)
	private String password;

	@Column(name = "name", nullable = false, length = 20)
	private String name;

	@Column(name = "email", nullable = false, unique = true, length = 50)
	private String email;

	@Column(name = "birth", nullable = false)
	private String birth;

	@Column(name = "phone_number", nullable = false, length = 11)
	private String phoneNumber;

	@Column(name = "point_balance", nullable = false, precision = 10, scale = 2)
	private BigDecimal pointBalance;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 10)
	private UserType type;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 10)
	private UserStatus status;

	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Address> addresses = new ArrayList<>();

	@Builder
	public User(MembershipLevel membershipLevel, String loginId, String encodedPassword, String name, String email,
		String birth, String phoneNumber, UserType type, UserStatus status,
		LocalDateTime lastLoginAt) {
		this.membershipLevel = membershipLevel;
		this.loginId = loginId;
		this.password = encodedPassword;
		this.name = name;
		this.email = email;
		this.birth = birth;
		this.phoneNumber = phoneNumber;
		this.pointBalance = (pointBalance == null) ? BigDecimal.ZERO : pointBalance;
		this.type = (type == null) ? UserType.USER : type;
		this.status = (status == null) ? UserStatus.ACTIVE : status;
		this.lastLoginAt = lastLoginAt;
	}

	public void addAddress(Address address) {
		this.addresses.add(address);
		address.assignUser(this);
	}

	public void removeAddress(Address address) {
		this.addresses.remove(address);
		address.assignUser(null);
	}

	public void changePassword(String newEncodedPassword) {
		this.password = newEncodedPassword;
	}

	public void updateLastLoginAt() {
		this.lastLoginAt = LocalDateTime.now();
	}

	public void updateStatus(UserStatus newStatus) {
		this.status = newStatus;
	}
}