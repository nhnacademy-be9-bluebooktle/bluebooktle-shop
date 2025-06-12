package shop.bluebooktle.backend.review.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.QBook;
import shop.bluebooktle.backend.book.entity.QImg;
import shop.bluebooktle.backend.book.entity.QReview;
import shop.bluebooktle.backend.book_order.entity.QBookOrder;
import shop.bluebooktle.backend.review.entity.Review;
import shop.bluebooktle.backend.review.repository.ReviewQueryRepository;
import shop.bluebooktle.common.entity.auth.QUser;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepositoryImpl implements ReviewQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Review> findReviewsByUserId(Long userId, Pageable pageable) {
		QReview review = QReview.review;
		QUser user = QUser.user;
		QBookOrder bookOrder = QBookOrder.bookOrder;
		QBook book = QBook.book;
		QImg img = QImg.img;

		List<Review> reviews = queryFactory
			.selectFrom(review)
			.join(review.user, user).fetchJoin()
			.join(review.bookOrder, bookOrder).fetchJoin()
			.join(bookOrder.book, book).fetchJoin()
			.leftJoin(review.img, img).fetchJoin()
			.where(user.id.eq(userId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(review.createdAt.desc())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(review.count())
			.from(review)
			.join(review.user, user)
			.where(user.id.eq(userId));

		return PageableExecutionUtils.getPage(reviews, pageable, countQuery::fetchOne);
	}

	@Override
	public Page<Review> findReviewsByBookId(Long bookId, Pageable pageable) {
		QReview review = QReview.review;
		QUser user = QUser.user;
		QBookOrder bookOrder = QBookOrder.bookOrder;
		QBook book = QBook.book;
		QImg img = QImg.img;

		List<Review> reviews = queryFactory
			.selectFrom(review)
			.join(review.user, user).fetchJoin()
			.join(review.bookOrder, bookOrder).fetchJoin()
			.join(bookOrder.book, book)
			.leftJoin(review.img, img).fetchJoin()
			.where(book.id.eq(bookId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(review.createdAt.desc())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(review.count())
			.from(review)
			.join(review.bookOrder, bookOrder)
			.join(bookOrder.book, book)
			.where(book.id.eq(bookId));

		return PageableExecutionUtils.getPage(reviews, pageable, countQuery::fetchOne);
	}
}