package shop.bluebooktle.backend.book.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.bluebooktle.backend.user.entity.User;

@Entity
@Table(name = "book_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"book", "user"})
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class BookLikes {
	@EmbeddedId
	private BookLikesId id;

	@MapsId("bookId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public BookLikes(Book book, User user) {
		this.book = book;
		this.user = user;
		this.id = new BookLikesId(book.getBookId(), user.getUserId());
	}
}
