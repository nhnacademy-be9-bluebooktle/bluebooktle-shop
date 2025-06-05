package shop.bluebooktle.backend.book_order.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book_order.entity.BookOrder;

public interface BookOrderRepository extends JpaRepository<BookOrder, Long> {
}
