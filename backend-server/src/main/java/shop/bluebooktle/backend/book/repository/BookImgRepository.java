package shop.bluebooktle.backend.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookImg;
import shop.bluebooktle.backend.book.entity.Img;

public interface BookImgRepository extends JpaRepository<BookImg, Long> {

	Optional<BookImg> findBookImgByBook(Book book);

	boolean existsByBookAndImg(Book book, Img img);

	List<BookImg> findByBookId(Long bookId);

	Optional<BookImg> findByBookIdAndImgId(Long bookId, Long imgId);

	// 특정 도서의 썸네일 한 장 조회
	Optional<BookImg> findFirstByBookIdAndIsThumbnailTrueOrderByIdAsc(Long bookId);

	BookImg findByBook(Book book);
}
