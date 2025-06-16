package shop.bluebooktle.backend.book.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Publisher;
import shop.bluebooktle.backend.book.entity.QPublisher;
import shop.bluebooktle.backend.book.repository.PublisherQueryRepository;

@RequiredArgsConstructor
public class PublisherQueryRepositoryImpl implements PublisherQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Publisher> searchByNameContaining(String searchKeyword, Pageable pageable) {
		QPublisher publisher = QPublisher.publisher;

		BooleanBuilder where = new BooleanBuilder().and(publisher.deletedAt.isNull()); // deletedAt IS NULL 조건

		// 검색 키워드가 주어졌으면 대소문자 구분 없이 부분 매칭 추가
		if (searchKeyword != null && !searchKeyword.isEmpty()) {
			where.and(publisher.name.containsIgnoreCase(searchKeyword));
		}

		// 실제 데이터 조회
		List<Publisher> content = queryFactory
			.selectFrom(publisher)
			.where(where)
			.orderBy(publisher.name.asc())        // 이름 오름차순 정렬
			.offset(pageable.getOffset())   // 조회 시작 위치
			.limit(pageable.getPageSize())  // 페이지 크기
			.fetch();

		// 전체 개수 카운트 (페이징용 total)
		Long total = queryFactory
			.select(publisher.count())
			.from(publisher)
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
}
