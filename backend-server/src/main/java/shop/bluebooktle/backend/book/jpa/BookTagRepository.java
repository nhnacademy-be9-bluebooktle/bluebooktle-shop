package shop.bluebooktle.backend.book.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookTag;
import shop.bluebooktle.backend.book.entity.Tag;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {

	List<BookTag> findByBook(Book book);

	List<BookTag> findByTag(Tag tag);

	// 특정 도서의 특정 태그 연결 유무 조회 (연결 시 true)
	boolean existsByBookAndTag(Book book, Tag tag);

	List<BookTag> findByBookId(Long bookId);

	Page<BookTag> findAllByTag(Tag tag, Pageable pageable);

	List<BookTag> tag(Tag tag);

	void deleteAllByTag(Tag tag);
}
