package shop.bluebooktle.backend.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.common.entity.review.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	Page<Review> findAllByBookOrder_Id(Long bookOrderId, Pageable pageable);
}
