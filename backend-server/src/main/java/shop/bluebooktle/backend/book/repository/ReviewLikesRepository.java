package shop.bluebooktle.backend.book.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.ReviewLikes;

public interface ReviewLikesRepository extends JpaRepository<ReviewLikes, Integer>, ReviewLikesQueryRepository {
	Optional<ReviewLikes> findByUserIdAndReviewId(Long userId, Long reviewId);

	void deleteByUserIdAndReviewId(Long userId, Long reviewId);
}
