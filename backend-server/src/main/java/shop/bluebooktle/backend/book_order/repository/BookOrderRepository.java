package shop.bluebooktle.backend.book_order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import feign.Param;
import shop.bluebooktle.backend.book_order.entity.BookOrder;

public interface BookOrderRepository extends JpaRepository<BookOrder, Long> {
	// 삭제되지 않은 도서 주문 전체 조회
	List<BookOrder> findAllByDeletedAtIsNull();

	// 삭제되지 않은 도서 개별 주문 조회
	Optional<BookOrder> findByIdAndDeletedAtIsNull(@Param("id") Long id);
}
