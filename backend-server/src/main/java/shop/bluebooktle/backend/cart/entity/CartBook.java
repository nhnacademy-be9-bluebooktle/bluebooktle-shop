package shop.bluebooktle.backend.cart.entity;

import java.time.LocalDateTime;

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
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "cart_book")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@ToString(exclude = {"book", "cart"})
@EqualsAndHashCode(of = "id", callSuper = false)
public class CartBook extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_book_id")
	private Long id;

	@Column(name = "quantity")
	private int quantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id", nullable = false)
	private Cart cart;

	@Builder
	public CartBook(Book book, Cart cart, int quantity) {
		this.book = book;
		this.cart = cart;
		this.quantity = quantity;
	}

	public static CartBook of(Book book, Cart cart, int quantity) {
		return new CartBook(book, cart, quantity);
	}

	public void increaseQuantity(int amount) {
		this.quantity += amount;
	}

	public void decreaseQuantity(int amount) {
		this.quantity = Math.max(1, this.quantity - amount);
	}

	// BaseEntity 변수 값 Setter 사용하기 위해 선언
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	// SQLDelete 안되서 Setter로 직접 변경
	public void setDeletedAt() {
		this.deletedAt = LocalDateTime.now();
	}

}
