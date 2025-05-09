package shop.bluebooktle.backend.coupon.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "category_coupon")
@SQLDelete(sql = "UPDATE category_coupon SET deleted_at = CURRENT_TIMESTAMP WHERE category_coupon_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class CategoryCoupon extends BaseEntity {
	@Id
	@Column(name = "coupon_id")
	private Long id;
	@MapsId
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coupon_id", nullable = false)
	private Coupon coupon;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@Builder
	public CategoryCoupon(Coupon coupon, Category category) {
		this.coupon = coupon;
		this.category = category;
	}
}
