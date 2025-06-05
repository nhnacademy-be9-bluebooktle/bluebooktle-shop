package shop.bluebooktle.backend.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {
	
	// TODO Query 어노테이션 수정
	@Query("SELECT ba.author FROM BookAuthor ba WHERE ba.book = :book")
	List<Author> findAuthorsByBook(@Param("book") Book book);

	// 특정 도서와 연결된 BookAuthor 엔티티 전체 조회
	List<BookAuthor> findByBook(Book book);

	// 도서 ID로 BookAuthor 엔티티 리스트 조회
	List<BookAuthor> findByBook_Id(Long bookId);

	// 특정 작가의 특정 도서 존재 유무 조회 (존재 시 true)
	boolean existsByBookAndAuthor(Book book, Author author);

	List<BookAuthor> findByBookId(Long bookId);

	Optional<BookAuthor> findByBookAndAuthor(Book book, Author author);
}
