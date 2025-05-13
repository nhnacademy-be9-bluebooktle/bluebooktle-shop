package shop.bluebooktle.backend.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookTag;
import shop.bluebooktle.backend.book.entity.Tag;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {

	// 특정 도서의 모든 태그 조회
	@Query("SELECT bt.tag FROM BookTag bt WHERE bt.book = :book")
	List<Tag> findTagsByBook(@Param("book") Book book);

	// 특정 태그의 모든 도서 조회
	@Query("SELECT bt.book FROM BookTag bt WHERE bt.tag = :tag")
	List<Book> findBooksByTag(@Param("tag") Tag tag);

	// 특정 도서의 특정 태그 연결 유무 조회 (연결 시 true)
	boolean existsByBookAndTag(Book book, Tag tag);

	List<BookTag> findByBookId(Long bookId);
}
