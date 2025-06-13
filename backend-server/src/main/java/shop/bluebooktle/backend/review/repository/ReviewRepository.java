package shop.bluebooktle.backend.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryRepository {
}
