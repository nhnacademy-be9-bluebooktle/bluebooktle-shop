package shop.bluebooktle.backend.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookLikes;
import shop.bluebooktle.backend.book.entity.BookLikesId;

public interface BookLikesRepository extends JpaRepository<BookLikes, BookLikesId> {

	// 사용자의 특정 도서에 대한 좋아요 여부 확인
	boolean existsByUser_IdAndBook_Id(Long userId, Long bookId);

	// 사용자가 좋아요 누른 책 전체 조회
	List<BookLikes> findByUser_Id(Long userId);

	@Query("select bl.book from BookLikes bl where bl.user.id = :userId")
	List<Book> findBooksLikedByUser(@Param("userId") Long userId);

	// 특정 책에 좋아요 누른 사람 전체 조회
	List<BookLikes> findByBook_Id(Long bookId);

	// 좋아요 취소 (삭제 전 대상 조회)
	BookLikes findByUser_IdAndBook_Id(Long userId, Long bookId);

	// 책 ID로 좋아요 수 집계 (count)
	long countByBook_Id(Long bookId);

	// 좋아요 순으로 Book 리스트 가져오기 (엘라스틱서치로 가능할 듯)
	@Query("select bl.book from BookLikes bl group by bl.book order by count(bl) desc")
	List<Book> findBooksOrderByLikesDesc();
}
