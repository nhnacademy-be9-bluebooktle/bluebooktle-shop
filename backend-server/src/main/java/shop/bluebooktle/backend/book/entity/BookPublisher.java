package shop.bluebooktle.backend.book.entity;

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
@Table(name = "book_publisher")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"book, publisher"})
@EqualsAndHashCode(of = "bookPublisherId", callSuper = false)
public class BookPublisher {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bookPublisherId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publisher_id", nullable = false)
	private Publisher publisher;

	public BookPublisher(Book book, Publisher publisher) {
		this.book = book;
		this.publisher = publisher;
	}
}
