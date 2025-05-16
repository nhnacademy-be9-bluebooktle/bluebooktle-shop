package shop.bluebooktle.backend.book.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {

	// 특정 작가의 모든 도서들 조회
	@Query("SELECT ba.book FROM BookAuthor ba WHERE ba.author = :author")
	List<Book> findBooksByAuthor(@Param("author") Author author);

	// 특정 도서의 모든 작가들 조회
	@Query("SELECT ba.author FROM BookAuthor ba WHERE ba.book = :book")
	List<Author> findAuthorsByBook(@Param("book") Book book);

	// 특정 작가의 특정 도서 존재 유무 조회 (존재 시 true)
	boolean existsByBookAndAuthor(Book book, Author author);

	List<BookAuthor> findByBookId(Long bookId);
}
