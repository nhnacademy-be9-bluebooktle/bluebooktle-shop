package shop.bluebooktle.backend.book_order.jpa;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.book_order.entity.QPackagingOption;
import shop.bluebooktle.backend.book_order.jpa.impl.PackagingOptionQueryRepositoryImpl;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.util.CryptoUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfiguration.class, CryptoUtils.class,
	ProfileAwareStringCryptoConverter.class})
class PackagingOptionRepositoryTest {

	@Autowired
	private PackagingOptionRepository packagingOptionRepository;

	private PackagingOptionQueryRepositoryImpl repository;

	@Autowired
	private EntityManager em;

	@Mock
	private JPAQueryFactory queryFactory;

	@Test
	@DisplayName("키워드 포함 포장 옵션 검색 - 성공")
	void searchNameContainingTest() {
		// given
		PackagingOption p1 = PackagingOption.builder().name("리본 포장지").price(BigDecimal.valueOf(3000)).build();
		PackagingOption p2 = PackagingOption.builder().name("땡땡이 상자").price(BigDecimal.valueOf(5000)).build();
		PackagingOption p3 = PackagingOption.builder().name("무지개 상자").price(BigDecimal.valueOf(4300)).build();

		packagingOptionRepository.save(p1);
		packagingOptionRepository.save(p2);
		packagingOptionRepository.save(p3);
		em.flush();
		em.clear();

		// when
		Page<PackagingOption> result = packagingOptionRepository.searchNameContaining("상자", PageRequest.of(0, 10));

		//then
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).extracting("name")
			.containsExactlyInAnyOrder("땡땡이 상자", "무지개 상자");
	}

	@Test
	@DisplayName("대소문자 구별 없이 포장 옵션 검색 - 성공")
	void searchIgnoreCaseTest() {
		// given
		PackagingOption p1 = PackagingOption.builder().name("Rainbow Box").price(BigDecimal.valueOf(3000)).build();
		packagingOptionRepository.save(p1);
		em.flush();
		em.clear();

		// when
		Page<PackagingOption> result = packagingOptionRepository.searchNameContaining("rainbow", PageRequest.of(0, 10));

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).extracting("name").containsExactly("Rainbow Box");
	}

	@Test
	@DisplayName("검색 키워드가 빈 문자열이면 전체 조회 - 성공")
	void searchNullKeywordTest() {
		// given
		PackagingOption p1 = PackagingOption.builder().name("리본 포장지").price(BigDecimal.valueOf(3000)).build();
		PackagingOption p2 = PackagingOption.builder().name("땡땡이 상자").price(BigDecimal.valueOf(5000)).build();
		packagingOptionRepository.save(p1);
		packagingOptionRepository.save(p2);
		em.flush();
		em.clear();

		// when
		Page<PackagingOption> result = packagingOptionRepository.searchNameContaining(null, PageRequest.of(0, 10));

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
	}

	@Test
	@DisplayName("검색 키워드가 공백이면 조회 실패 - 성공")
	void searchWhitespaceKeywordTest() {
		// given
		PackagingOption p1 = PackagingOption.builder().name("리본 포장지").price(BigDecimal.valueOf(3000)).build();
		PackagingOption p2 = PackagingOption.builder().name("땡땡이 상자").price(BigDecimal.valueOf(5000)).build();
		packagingOptionRepository.save(p1);
		packagingOptionRepository.save(p2);
		em.flush();
		em.clear();

		// when
		Page<PackagingOption> result = packagingOptionRepository.searchNameContaining("       ", PageRequest.of(0, 10));

		// then
		assertThat(result.getTotalElements()).isEqualTo(0);
	}

	@Test
	@DisplayName("검색 키워드가 없으면 전체 조회 - 성공")
	void searchNoKeywordTest() {
		// given
		PackagingOption p1 = PackagingOption.builder().name("리본 포장지").price(BigDecimal.valueOf(3000)).build();
		PackagingOption p2 = PackagingOption.builder().name("땡땡이 상자").price(BigDecimal.valueOf(5000)).build();
		packagingOptionRepository.save(p1);
		packagingOptionRepository.save(p2);
		em.flush();
		em.clear();

		// when
		Page<PackagingOption> result = packagingOptionRepository.searchNameContaining("", PageRequest.of(0, 10));

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
	}

	@Test
	@DisplayName("저장된 이름의 존재 여부 - 성공")
	void existsByName_true() {
		// given
		PackagingOption p1 = PackagingOption.builder().name("체크 포장지").price(BigDecimal.valueOf(500)).build();
		em.persist(p1);
		em.flush();
		em.clear();

		// when & then
		assertThat(packagingOptionRepository.existsByName("체크 포장지")).isTrue();
	}

	@Test
	@DisplayName("저장되지 않은 이름의 존재 여부 - 실패")
	void existsByName_false() {
		assertThat(packagingOptionRepository.existsByName("꽃무늬 포장지")).isFalse();
	}

	@Test
	@DisplayName("이름 중복 존재 여부 확인 - 성공")
	void duplicateNameExistTest() {
		// given
		PackagingOption p1 = PackagingOption.builder().name("리본 포장지").price(BigDecimal.valueOf(3000)).build();
		packagingOptionRepository.save(p1);
		em.flush();
		em.clear();

		// when
		boolean exists = packagingOptionRepository.existsByName("리본 포장지");

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("페이징 처리를 위한 포장 옵션 개수 확인 - 성공")
	void totalIsNullTest() {
		// given
		String searchKeyword = "떙떙이 포장지";
		Pageable pageable = PageRequest.of(0, 5);
		QPackagingOption packagingOption = QPackagingOption.packagingOption;

		// queryFactory를 이용해 repository를 생성
		PackagingOptionQueryRepositoryImpl repository = new PackagingOptionQueryRepositoryImpl(queryFactory);

		// content 쿼리 모킹: 실제 DB 대신 Mockito로 빈 리스트 반환하도록 설정
		@SuppressWarnings("unchecked")
		JPAQuery<PackagingOption> contentQuery = mock(JPAQuery.class);
		when(queryFactory.selectFrom(packagingOption)).thenReturn(contentQuery);
		when(contentQuery.where(any(BooleanBuilder.class))).thenReturn(
			contentQuery); // selectFrom(packagingOption) 호출 시 mock된 contentQuery 리턴
		when(contentQuery.orderBy(packagingOption.name.asc())).thenReturn(contentQuery); // orderBy 호출
		when(contentQuery.offset(pageable.getOffset())).thenReturn(contentQuery); // offeset 호출
		when(contentQuery.limit(pageable.getPageSize())).thenReturn(contentQuery); // limit 호출
		when(contentQuery.fetch()).thenReturn(Collections.emptyList()); // fetch 호출

		// count 쿼리 모킹: fetchOne()이 null을 반환
		@SuppressWarnings("unchecked")
		JPAQuery<Long> countQuery = mock(JPAQuery.class);
		when(queryFactory.select(packagingOption.count())).thenReturn(countQuery);
		when(countQuery.from(packagingOption)).thenReturn(countQuery);
		when(countQuery.where(any(BooleanBuilder.class))).thenReturn(countQuery);
		when(countQuery.fetchOne()).thenReturn(null);

		// when: repository (모킹된 queryFactory를 사용하는) 호출
		Page<PackagingOption> result = repository.searchNameContaining(searchKeyword, pageable);

		// then: 카운트가 null이었으므로 totalCount 가 0으로 처리되어야 함
		assertThat(result.getTotalElements()).isZero();
		assertThat(result.getContent()).isEmpty(); // 콘텐츠 쿼리 결과는 빈 리스트
	}
}
