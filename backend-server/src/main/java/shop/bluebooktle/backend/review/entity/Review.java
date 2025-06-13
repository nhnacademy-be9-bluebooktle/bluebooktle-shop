package shop.bluebooktle.backend.review.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CascadeType;
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
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.common.entity.BaseEntity;
import shop.bluebooktle.common.entity.auth.User;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE `review` SET deleted_at = CURRENT_TIMESTAMP WHERE review_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "book_order_id", nullable = false)
	private BookOrder bookOrder;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "img_id")
	private Img img;

	@Column(name = "star", nullable = false)
	private int star;

	@Column(name = "review_content", columnDefinition = "TEXT")
	private String reviewContent;

	@Column(name = "likes")
	private Integer likes;

	@Builder
	public Review(Long id,
		User user,
		BookOrder bookOrder,
		Img img,
		int star,
		String reviewContent,
		Integer likes) {
		this.id = id;
		this.user = user;
		this.bookOrder = bookOrder;
		this.img = img;
		this.star = star;
		this.reviewContent = reviewContent;
		this.likes = likes;
	}

	public void updateDetails(int newStar, String newReviewContent, Img newImg) {
		this.star = newStar;
		this.reviewContent = newReviewContent;
		this.img = newImg;
	}

	public void incrementLikeCount() {
		this.likes++;
	}

	public void decrementLikeCount() {
		if (this.likes > 0) {
			this.likes--;
		}
	}
}
