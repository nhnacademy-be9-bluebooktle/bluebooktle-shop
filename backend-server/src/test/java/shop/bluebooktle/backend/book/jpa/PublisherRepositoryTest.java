package shop.bluebooktle.backend.book.jpa;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import shop.bluebooktle.backend.book.entity.Publisher;
import shop.bluebooktle.backend.book.entity.QPublisher;
import shop.bluebooktle.backend.book.repository.PublisherRepository;
import shop.bluebooktle.backend.book.repository.impl.PublisherQueryRepositoryImpl;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.util.CryptoUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfiguration.class, CryptoUtils.class,
	ProfileAwareStringCryptoConverter.class})
class PublisherRepositoryTest {
	@Autowired
	private PublisherRepository publisherRepository;

	@Autowired
	private EntityManager em;

	@BeforeEach
	void setup() {
		publisherRepository.deleteAll();  // 남아있는 엔티티 모두 제거

	}

	@Test
	@DisplayName("검색 키워드가 포함된 출판사만 조회 - 성공")
	void searchByNameContainingKeyword_success() {
		// given
		Publisher p1 = Publisher.builder().name("Alpha Press").build();
		Publisher p2 = Publisher.builder().name("Beta Books").build();
		Publisher p3 = Publisher.builder().name("Gamma Publishing").build();
		publisherRepository.saveAll(Arrays.asList(p1, p2, p3));
		em.flush();
		em.clear();

		// when
		Page<Publisher> result = publisherRepository.searchByNameContaining("press", PageRequest.of(0, 10));

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent())
			.extracting("name")
			.containsExactly("Alpha Press");
	}

	@Test
	@DisplayName("대수문자 구별 없이 출판사 조회 - 성공")
	void searchByNameContainingIgnoreCase_success() {
		// given
		Publisher p1 = Publisher.builder().name("Alpha Press").build();
		publisherRepository.save(p1);
		em.flush();
		em.clear();

		// when
		Page<Publisher> result = publisherRepository.searchByNameContaining("PRESS", PageRequest.of(0, 10));

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent())
			.extracting("name")
			.containsExactly("Alpha Press");
	}

	@Test
	@DisplayName("검색 키워드가 null이면 전체 조회 - 성공")
	void searchByNullKeyword_success() {
		// given
		Publisher p1 = Publisher.builder().name("Alpha Press").build();
		Publisher p2 = Publisher.builder().name("Beta Books").build();
		publisherRepository.saveAll(Arrays.asList(p1, p2));
		em.flush();
		em.clear();

		// when
		Page<Publisher> result = publisherRepository.searchByNameContaining(null, PageRequest.of(0, 10));

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
	}

	@Test
	@DisplayName("검색 키워드가 빈 문자열이면 전체 조회 - 성공")
	void searchByBlank_success() {
		// given
		Publisher p1 = Publisher.builder().name("Alpha Press").build();
		Publisher p2 = Publisher.builder().name("Beta Books").build();
		publisherRepository.saveAll(Arrays.asList(p1, p2));
		em.flush();
		em.clear();

		// when
		Page<Publisher> result = publisherRepository.searchByNameContaining("", PageRequest.of(0, 10));

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
	}

	@Test
	@DisplayName("검색 키워드가 공백 문자열이면 조회 결과 없음 - 실패")
	void searchByWhitespace_fail() {
		// given
		Publisher p1 = Publisher.builder().name("Foo Bar").build();
		publisherRepository.save(p1);
		em.flush();
		em.clear();

		// when
		Page<Publisher> result = publisherRepository.searchByNameContaining("     ", PageRequest.of(0, 10));

		// then
		assertThat(result.getTotalElements()).isZero();
		assertThat(result.getContent()).isEmpty();
	}

	@Test
	@DisplayName("검색어가 일치하는 출판사가 없으면 total==null 커버")
	void searchByNameContaining_noMatch() {
		// given: 저장된 출판사 없음

		// when
		Page<Publisher> result =
			publisherRepository.searchByNameContaining("NoMatch", PageRequest.of(0, 10));

		// then
		assertThat(result.getTotalElements()).isZero();   // totalCount == 0
		assertThat(result.getContent()).isEmpty();
	}

	@Test
	@DisplayName("count 쿼리가 null 이면 totalCount=0 처리")
	void searchByNameContaining_totalIsNull() {

		// mock 객체
		JPAQueryFactory factory = mock(JPAQueryFactory.class);
		JPAQuery<Publisher> contentQuery =
			(JPAQuery<Publisher>)mock(JPAQuery.class, RETURNS_DEEP_STUBS);
		JPAQuery<Long> countQuery =
			(JPAQuery<Long>)mock(JPAQuery.class, RETURNS_DEEP_STUBS);

		QPublisher p = QPublisher.publisher;

		when(factory.selectFrom(p)).thenReturn(contentQuery);
		when(contentQuery.where(any(BooleanBuilder.class))).thenReturn(contentQuery);
		when(contentQuery.orderBy(any(OrderSpecifier.class))).thenReturn(contentQuery);
		when(contentQuery.offset(anyLong())).thenReturn(contentQuery);
		when(contentQuery.limit(anyLong())).thenReturn(contentQuery);
		when(contentQuery.fetch()).thenReturn(Collections.emptyList());

		// 카운트 쿼리 체인
		when(factory.select(p.count())).thenReturn(countQuery);
		when(countQuery.from(p)).thenReturn(countQuery);
		when(countQuery.where(any(BooleanBuilder.class))).thenReturn(countQuery);
		when(countQuery.fetchOne()).thenReturn(null);

		// 리포지토리에 주입 후 호출
		PublisherQueryRepositoryImpl repo = new PublisherQueryRepositoryImpl(factory);

		Page<Publisher> page =
			repo.searchByNameContaining("no-match", PageRequest.of(0, 5));

		// 검증 – totalElements == 0 이면 분기
		assertThat(page.getTotalElements()).isZero();
		assertThat(page.getContent()).isEmpty();
	}
}
