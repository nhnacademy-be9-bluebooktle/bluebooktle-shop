package shop.bluebooktle.backend.book.repository;

import java.util.Optional;

import shop.bluebooktle.backend.book.entity.ReviewLikes;

public interface ReviewLikesQueryRepository {
	Optional<ReviewLikes> findSoftDeletedByUserIdAndReviewId(Long userId, Long reviewId);

	void undeleteByUserIdAndReviewId(Long userId, Long reviewId);
}
