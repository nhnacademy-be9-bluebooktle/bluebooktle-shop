package shop.bluebooktle.backend.book_order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book_order.entity.OrderPackaging;

public interface OrderPackagingRepository extends JpaRepository<OrderPackaging, Long> {
}
