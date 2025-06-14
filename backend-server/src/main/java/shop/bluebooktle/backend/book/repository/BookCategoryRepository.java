package shop.bluebooktle.backend.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookCategory;
import shop.bluebooktle.backend.book.entity.Category;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

	// 책 ID로 연결된 BookCategory 조회 (연관된 카테고리 목록용)
	List<BookCategory> findByBook_Id(Long bookId);

	// 특정 책에 연결된 카테고리 개수 조회
	long countByBook(Book book);

	// 특정 책과 카테고리의 연결 여부 확인 (중복 방지 체크용)
	boolean existsByBookAndCategory(Book book, Category category);

	boolean existsByCategory(Category category);
}
