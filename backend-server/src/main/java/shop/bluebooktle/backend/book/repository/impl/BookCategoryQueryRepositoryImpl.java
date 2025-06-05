package shop.bluebooktle.backend.book.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.QBook;
import shop.bluebooktle.backend.book.entity.QBookCategory;
import shop.bluebooktle.backend.book.entity.QCategory;
import shop.bluebooktle.backend.book.repository.BookCategoryQueryRepository;

@Repository
@RequiredArgsConstructor
public class BookCategoryQueryRepositoryImpl implements BookCategoryQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Book> findBookUnderCategory(Long categoryId, Pageable pageable) {
		QCategory category = QCategory.category;
		QBookCategory bookCategory = QBookCategory.bookCategory;
		QBook book = QBook.book;

		String categoryPath = queryFactory
			.select(category.categoryPath)
			.from(category)
			.where(category.id.eq(categoryId))
			.fetchOne();

		List<Long> leafCategoryIds = queryFactory
			.select(category.id)
			.from(category)
			.where(
				category.categoryPath.like(categoryPath + "/%")
					.or(category.categoryPath.eq(categoryPath)),
				category.childCategories.isEmpty()
			)
			.fetch();

		List<Book> content = queryFactory
			.select(book)
			.from(bookCategory)
			.join(bookCategory.book, book)
			.where(bookCategory.category.id.in(leafCategoryIds))
			.orderBy(bookCategory.book.id.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(book.count())
			.from(bookCategory)
			.join(bookCategory.book, book)
			.where(bookCategory.category.id.in(leafCategoryIds))
			.fetchOne();

		long totalCount; // total 이 null 인지 아닌지 검사
		if (total == null) {
			totalCount = 0;
		} else {
			totalCount = total;
		}
		return new PageImpl<>(content, pageable, totalCount);
	}
}
