package shop.bluebooktle.backend.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookCategory;
import shop.bluebooktle.backend.book.entity.Category;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

	// 특정 책에 연결된 모든 카테고리 조회
	List<BookCategory> findByBook(Book book);

	// 특정 카테고리에 속한 모든 책 조회
	List<BookCategory> findByCategory(Category category);

	// 책 ID로 연결된 BookCategory 조회 (연관된 카테고리 목록용)
	List<BookCategory> findByBook_Id(Long bookId);

	// 카테고리 ID로 연결된 BookCategory 조회 (연관된 책 목록용)
	List<BookCategory> findByCategory_Id(Long categoryId);

	// 특정 책에 연결된 카테고리 개수 조회
	long countByBook(Book book);

	// 특정 책과 카테고리의 연결 여부 확인 (중복 방지 체크용)
	boolean existsByBookAndCategory(Book book, Category category);

	// 특정 카테고리와 연결된 모든 책 ID 조회
	List<Long> findBookIdByCategory_Id(Long categoryId);

	@Modifying
	@Transactional
	void deleteByCategory(Category category);

	@Modifying
	@Transactional
	void deleteByCategoryIn(List<Category> categories);

}
