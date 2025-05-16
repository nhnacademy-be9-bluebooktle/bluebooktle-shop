package shop.bluebooktle.backend.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
	name = "book_category",
	uniqueConstraints = @UniqueConstraint(
		name = "UK_book_category_book_category",
		columnNames = {"book_id", "category_id"}
	)
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"book", "category"})
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class BookCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_category_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	@Setter
	private Category category;

	public BookCategory(Book book, Category category) {
		this.book = book;
		this.category = category;
	}

}
