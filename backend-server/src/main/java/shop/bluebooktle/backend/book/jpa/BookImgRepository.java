package shop.bluebooktle.backend.book.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookImg;

public interface BookImgRepository extends JpaRepository<BookImg, Long> {

	// 특정 도서의 연관 이미지 (url) 조회
	@Query("SELECT bi.img.imgUrl FROM BookImg bi WHERE bi.book = :book")
	List<String> findImgUrlsByBook(@Param("book") Book book);

	// 특정 도서의 삭제된 이미지 (url) 조회
	@Query("SELECT bi.img.imgUrl FROM BookImg bi WHERE bi.book = :book AND bi.img.deletedAt IS NOT NULL")
	List<String> findDeletedImgUrlsByBook(@Param("book") Book book);

	// 특정 도서의 삭제되지 않은 이미지 (url) 조회
	@Query("SELECT bi.img.imgUrl FROM BookImg bi WHERE bi.book = :book AND bi.img.deletedAt IS NULL")
	List<String> findActiveImgUrlsByBook(@Param("book") Book book);

	List<BookImg> findByBookId(Long bookId);
}
