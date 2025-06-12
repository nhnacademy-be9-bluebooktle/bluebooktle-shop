package shop.bluebooktle.backend.book.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.entity.QCategory;
import shop.bluebooktle.backend.book.repository.CategoryQueryRepository;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Category> searchByNameContaining(String searchKeyword, Pageable pageable) {
		QCategory category = QCategory.category;  // QueryDSL Q타입 엔티티 참조

		BooleanBuilder where = new BooleanBuilder().and(category.deletedAt.isNull()); // deletedAt IS NULL 조건

		// 검색 키워드가 주어졌으면 대소문자 구분 없이 부분 매칭 추가
		if (searchKeyword != null && !searchKeyword.isEmpty()) {
			where.and(category.name.containsIgnoreCase(searchKeyword));
		}

		// 실제 데이터 조회
		List<Category> content = queryFactory
			.selectFrom(category)
			.where(where)
			.orderBy(category.name.asc())        // 이름 오름차순 정렬
			.offset(pageable.getOffset())   // 조회 시작 위치
			.limit(pageable.getPageSize())  // 페이지 크기
			.fetch();

		// 전체 개수 카운트 (페이징용 total)
		Long total = queryFactory
			.select(category.count())
			.from(category)
			.where(where)
			.fetchOne();

		long totalCount; // total 이 null 인지 아닌지 검사
		if (total == null) {
			totalCount = 0;
		} else {
			totalCount = total;
		}

		return new PageImpl<>(content, pageable, totalCount);
	}

	@Override
	public List<Long> findUnderCategory(Category parentCategory) {

		QCategory category = QCategory.category;

		String categoryPath = queryFactory
			.select(category.categoryPath)
			.from(category)
			.where(category.id.eq(parentCategory.getId()))
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

		return leafCategoryIds;
	}

	@Override
	public List<Category> getAllDescendantCategories(Category parent) {
		QCategory category = QCategory.category;

		// 부모 categoryPath 조회
		String parentPath = queryFactory
			.select(category.categoryPath)
			.from(category)
			.where(category.id.eq(parent.getId()))
			.fetchOne();

		if (parentPath == null) {
			return List.of(); // 부모가 존재하지 않으면 빈 리스트
		}

		// parent 포함 + 하위 포함 전체 자식 카테고리 조회
		return queryFactory
			.selectFrom(category)
			.where(
				category.categoryPath.eq(parentPath)
					.or(category.categoryPath.like(parentPath + "/%")),
				category.deletedAt.isNull()
			)
			.fetch();
	}

}
