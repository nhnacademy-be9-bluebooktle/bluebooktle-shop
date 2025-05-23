package shop.bluebooktle.backend.book_order.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.book_order.entity.QPackagingOption;
import shop.bluebooktle.backend.book_order.repository.PackagingOptionQueryRepository;

@Repository
@RequiredArgsConstructor
public class PackagingOptionQueryRepositoryImpl implements PackagingOptionQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<PackagingOption> searchNameContaining(String searchKeyword, Pageable pageable) {
		QPackagingOption packagingOption = QPackagingOption.packagingOption;

		BooleanBuilder where = new BooleanBuilder().and(packagingOption.deletedAt.isNull()); // deletedAt IS NULL 조건

		// 검색 키워드가 주어졌으면 대소문자 구분 없이 부분 매칭 추가
		if (searchKeyword != null && !searchKeyword.isEmpty()) {
			where.and(packagingOption.name.containsIgnoreCase(searchKeyword));
		}

		List<PackagingOption> content = queryFactory
			.selectFrom(packagingOption)
			.where(where)
			.orderBy(packagingOption.name.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(packagingOption.count())
			.from(packagingOption)
			.where(where)
			.fetchOne();

		long totalCount;
		if (total == null) {
			totalCount = 0;
		} else {
			totalCount = total;
		}

		return new PageImpl<>(content, pageable, totalCount);
	}
}
