package shop.bluebooktle.backend.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {

	// 도서 ID로 BookAuthor 엔티티 리스트 조회
	List<BookAuthor> findByBook_Id(Long bookId);

	// 도서 ID로 BookAuthor 엔티티 리스트 조회
	List<BookAuthor> findByAuthor(Author author);

	// 특정 작가의 특정 도서 존재 유무 조회 (존재 시 true)
	boolean existsByBookAndAuthor(Book book, Author author);

	List<BookAuthor> findByBookId(Long bookId);

	Optional<BookAuthor> findByBookAndAuthor(Book book, Author author);

	boolean existsByAuthor(Author author);
}
