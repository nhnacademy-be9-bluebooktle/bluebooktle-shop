package shop.bluebooktle.backend.book.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.backend.cart.entity.CartBook;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book")
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE book SET deleted_at = CURRENT_TIMESTAMP WHERE book_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Book extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_id")
	private Long id;

	@Column(name = "title", nullable = false, length = 255)
	@Setter
	private String title;

	@Column(name = "`index`", columnDefinition = "TEXT")
	@Setter
	private String index;

	@Column(name = "description", nullable = false, columnDefinition = "TEXT")
	@Setter
	private String description;

	@Column(name = "publish_date")
	@Setter
	private LocalDateTime publishDate;

	@Column(name = "isbn", nullable = false, length = 13)
	private String isbn;

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@BatchSize(size = 10)
	private List<CartBook> cartBooks = new ArrayList<>();

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@BatchSize(size = 10)
	private List<BookImg> bookImgs = new ArrayList<>();

	@OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	private BookSaleInfo bookSaleInfo;

	public void addCart(Cart cart, int quantity) {
		CartBook cartBook = CartBook.builder().book(this).cart(cart).quantity(quantity).build();

		this.cartBooks.add(cartBook);
		cart.getCartBooks().add(cartBook);
	}

	public void addBookImg(BookImg bookImg) {
		this.bookImgs.add(bookImg);
		if (bookImg.getBook() != this) {
			bookImg.setBook(this);
		}
	}
}
