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
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "book_coupon")
@SQLDelete(sql = "UPDATE book_coupon SET deleted_at = CURRENT_TIMESTAMP WHERE book_coupon_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class BookCoupon extends BaseEntity {
	@Id
	@Column(name = "coupon_id")
	private Long id;
	@MapsId
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coupon_id", nullable = false)
	private Coupon coupon;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Builder
	public BookCoupon(Coupon coupon, Book book) {
		this.coupon = coupon;
		this.book = book;
	}
}
