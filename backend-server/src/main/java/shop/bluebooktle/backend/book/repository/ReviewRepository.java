package shop.bluebooktle.backend.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryRepository {
}
