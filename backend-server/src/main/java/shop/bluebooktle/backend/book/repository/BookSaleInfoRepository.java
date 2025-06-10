package shop.bluebooktle.backend.book.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;

public interface BookSaleInfoRepository extends JpaRepository<BookSaleInfo, Long> {
	Optional<BookSaleInfo> findByBook(Book book);

	Optional<BookSaleInfo> findByBookId(Long bookId);
}
