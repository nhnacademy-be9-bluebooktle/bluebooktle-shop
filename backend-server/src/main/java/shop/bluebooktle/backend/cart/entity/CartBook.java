package shop.bluebooktle.backend.cart.entity;

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
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "cart_book")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"book", "cart"})
@SQLDelete(sql = "UPDATE cart_book SET deleted_at = CURRENT_TIMESTAMP WHERE cart_book_id = ?")
@SQLRestriction("deleted_at IS NULL")
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

	public boolean decreaseQuantity() {
		if (this.quantity > 1) {
			this.quantity -= 1;
			return false;
		}
		return true;
	}

}
