package shop.bluebooktle.backend.review.repository.impl;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.QReviewLikes;
import shop.bluebooktle.backend.review.entity.ReviewLikes;
import shop.bluebooktle.backend.review.repository.ReviewLikesQueryRepository;

@Repository
@RequiredArgsConstructor
public class ReviewLikesQueryRepositoryImpl implements ReviewLikesQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<ReviewLikes> findSoftDeletedByUserIdAndReviewId(Long userId, Long reviewId) {
		QReviewLikes reviewLikes = QReviewLikes.reviewLikes;
		return Optional.ofNullable(queryFactory
			.selectFrom(reviewLikes)
			.where(reviewLikes.user.id.eq(userId),
				reviewLikes.review.id.eq(reviewId),
				reviewLikes.deletedAt.isNotNull())
			.fetchOne());
	}

	@Override
	@Transactional
	public void undeleteByUserIdAndReviewId(Long userId, Long reviewId) {
		QReviewLikes reviewLikes = QReviewLikes.reviewLikes;
		queryFactory
			.update(reviewLikes)
			.setNull(reviewLikes.deletedAt)
			.where(reviewLikes.user.id.eq(userId),
				reviewLikes.review.id.eq(reviewId),
				reviewLikes.deletedAt.isNotNull())
			.execute();
	}
}
