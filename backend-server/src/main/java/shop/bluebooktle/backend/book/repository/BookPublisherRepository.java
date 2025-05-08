package shop.bluebooktle.backend.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookPublisher;
import shop.bluebooktle.backend.book.entity.Publisher;

public interface BookPublisherRepository extends JpaRepository<BookPublisher, Long> {

	// 특정 출판사의 모든 도서들 조회
	@Query("SELECT bp.book FROM BookPublisher bp WHERE bp.publisher = :publisher")
	List<Book> findBooksByPublisher(@Param("publisher") Publisher publisher);

	// 특정 도서의 모든 출판사들 조회
	@Query("SELECT bp.publisher FROM BookPublisher bp WHERE bp.book = :book")
	List<Publisher> findPublishersByBook(@Param("book") Book book);

	// 특정 출판사의 특정 도서 존재 유무 조회
	boolean existsByBookAndPublisher(Book book, Publisher publisher);
}
