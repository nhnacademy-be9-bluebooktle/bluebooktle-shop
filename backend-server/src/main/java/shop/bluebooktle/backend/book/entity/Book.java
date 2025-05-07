package shop.bluebooktle.backend.book.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book")
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_id")
	private Long bookId;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "index")
	private String index;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "publish_date")
	private LocalDateTime publishDate;

	@Column(name = "isbn", nullable = false, length = 13)
	private String isbn;

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<CartBook> cartBooks = new ArrayList<>();

	public void addCart(Cart cart) {
		CartBook cartBook = CartBook.builder().book(this).cart(cart).build();

		this.cartBooks.add(cartBook);
	}
}
