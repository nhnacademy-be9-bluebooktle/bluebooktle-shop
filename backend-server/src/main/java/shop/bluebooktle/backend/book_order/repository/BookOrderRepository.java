package shop.bluebooktle.backend.book_order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import feign.Param;
import shop.bluebooktle.backend.book_order.entity.BookOrder;

public interface BookOrderRepository extends JpaRepository<BookOrder, Long> {
	// 삭제되지 않은 도서 주문 전체 조회
	@Query("SELECT bo FROM BookOrder bo WHERE bo.deletedAt IS NULL")
	List<BookOrder> findAllAvailable();

	// 삭제되지 않은 도서 개별 주문 조회
	@Query("SELECT bo FROM BookOrder bo WHERE bo.id = :id AND bo.deletedAt IS NULL")
	Optional<BookOrder> findByIdAndDeletedAtIsNull(@Param("id") Long id);
}
