package shop.bluebooktle.backend.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookPublisher;
import shop.bluebooktle.backend.book.entity.Publisher;

public interface BookPublisherRepository extends JpaRepository<BookPublisher, Long> {

	List<BookPublisher> findByBook(Book book);

	List<BookPublisher> findByPublisher(Publisher publisher);

	// 책으로 출판사 목록 조회
	List<BookPublisher> findByBook_Id(Long bookId);

	// 특정 출판사의 특정 도서 존재 유무 조회
	boolean existsByBookAndPublisher(Book book, Publisher publisher);

	Page<BookPublisher> findAllByPublisher(Publisher publisher, Pageable pageable);

	List<BookPublisher> findByBookId(Long bookId);

	boolean existsByPublisher(Publisher publisher);

	Optional<BookPublisher> findByBookAndPublisher(Book book, Publisher publisher);
}
