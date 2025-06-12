package shop.bluebooktle.backend.review.repository;

import java.util.Optional;

import shop.bluebooktle.backend.review.entity.ReviewLikes;

public interface ReviewLikesQueryRepository {
	Optional<ReviewLikes> findSoftDeletedByUserIdAndReviewId(Long userId, Long reviewId);

	void undeleteByUserIdAndReviewId(Long userId, Long reviewId);
}
