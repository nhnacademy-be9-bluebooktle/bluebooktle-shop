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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "bookTag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"tag, book"})
@EqualsAndHashCode(of = "id", callSuper = false)
public class BookTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_tag_id")
	private Long id;

	@JoinColumn(name = "tag_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Tag tag;

	@JoinColumn(name = "book_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Book book;

	public BookTag(Tag tag, Book book) {
		this.tag = tag;
		this.book = book;
	}
}
