package shop.bluebooktle.backend.book.jpa;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.persistence.EntityManager;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.util.CryptoUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfiguration.class, CryptoUtils.class,
	ProfileAwareStringCryptoConverter.class})
class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private EntityManager em;

	private Category category1;

	@BeforeEach
	void setUp() {
		category1 = categoryRepository.save(new Category(null, "소설", "/1"));
		Category category2 = categoryRepository.save(new Category(category1, "일반 소설", "/1/2"));
		categoryRepository.save(new Category(category2, "장르 소설", "/1/2/3"));

		Category category4 = categoryRepository.save(new Category(category1, "국내 현대 소설", "/1/4"));
		em.flush();

		// category4 삭제
		em.createQuery(
				"update Category c set c.deletedAt = :now where c.id = :id")
			.setParameter("now", LocalDateTime.now())
			.setParameter("id", category4.getId())
			.executeUpdate();
		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("검색어 부분 매칭된 카테고리만 반환(deletedAt 필터링)")
	void searchByNameContaining_keywordMatching() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<Category> page = categoryRepository.searchByNameContaining("소설", pageable);

		// then
		// category1, category2, category3 중 '소설'이 포함된 세 개 → deleted 제외하고 3개
		assertThat(page.getTotalElements()).isEqualTo(3);
		List<String> names = page.getContent().stream()
			.map(Category::getName)
			.toList();
		// 이름 오름차순 정렬: "소설, "일반 소설", "장르 소설" (ASCII 기준 대문자 < 소문자)
		assertThat(names)
			.containsExactly("소설", "일반 소설", "장르 소설")
			.doesNotContain("국내 현대 소설");

	}

	@Test
	@DisplayName("빈 검색어 전달 시 모든 카테고리 이름순 반환")
	void searchByNameContaining_emptyKeyword() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<Category> page = categoryRepository.searchByNameContaining("", pageable);

		// then
		assertThat(page.getTotalElements()).isEqualTo(3);
		assertThat(page.getContent())
			.extracting(Category::getName)
			.doesNotContain("국내 현대 소설");
	}

	@Test
	@DisplayName("검색어 null 전달 시 모든 카테고리 이름순 반환")
	void searchByNameContaining_nullKeyword() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<Category> page = categoryRepository.searchByNameContaining(null, pageable);

		// then
		assertThat(page.getTotalElements()).isEqualTo(3);
		assertThat(page.getContent())
			.extracting(Category::getName)
			.doesNotContain("국내 현대 소설");
	}


	@Test
	@DisplayName("페이징 옵션 적용 테스트")
	void searchByNameContaining_pagination() {
		// given: 페이지 크기 2
		Pageable first = PageRequest.of(0, 2);
		Pageable second = PageRequest.of(1, 2);

		// when
		Page<Category> page1 = categoryRepository.searchByNameContaining("", first);
		Page<Category> page2 = categoryRepository.searchByNameContaining("", second);

		// then
		assertThat(page1.getContent()).hasSize(2);
		assertThat(page2.getContent()).hasSize(1);

		// page1 + page2 합쳐서 deleted 제외한 3개가 모두 조회됨
		List<String> allNames = Stream.concat(
				page1.getContent().stream(),
				page2.getContent().stream()
			)
			.map(Category::getName)
			.toList();
		assertThat(allNames).containsExactlyInAnyOrder("소설", "일반 소설", "장르 소설");
	}

	@Test
	@DisplayName("검색 결과 없음 테스트")
	void searchByNameContaining_pagination_noContent() {
		// given: 페이지 크기 10
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<Category> page = categoryRepository.searchByNameContaining("없는키워드", pageable);

		// then
		assertThat(page.getTotalElements()).isZero();
		assertThat(page.getContent()).isEmpty();
	}

	@Test
	@DisplayName("하위 leaf 카테고리 ID 조회 테스트")
	void findUnderCategory_leafCategoryOnly() {
		// given: 소설 > 일반 소설 > 로맨스 소설 구조
		Category parent = new Category(null, "소설", "/5");
		categoryRepository.save(parent);

		Category child1 = new Category(parent, "일반 소설", "/5/6");
		categoryRepository.save(child1);

		Category child2 = new Category(child1, "로맨스 소설", "/5/6/7");
		categoryRepository.save(child2);

		em.flush();
		em.clear();

		// when
		List<Long> result = categoryRepository.findUnderCategory(parent);

		// then: leaf 노드는 로맨스 소설 하나뿐
		assertThat(result)
			.hasSize(1)
			.containsExactly(child2.getId());
	}

	@Test
	@DisplayName("deletedAt 설정된 하위 카테고리는 제외")
	void getAllDescendantCategories_deletedExcluded() {
		// when
		List<Category> result = categoryRepository.getAllDescendantCategories(category1);

		// then: 국내 현대 소설은 빠지고 일반소설, 장르소설만 남음
		assertThat(result)
			.extracting(Category::getName)
			.containsExactlyInAnyOrder("소설", "일반 소설", "장르 소설");
	}

	@Test
	@DisplayName("존재하지 않는 parent 전달 시 빈 리스트 반환")
	void getAllDescendantCategories_parentNotFound() {
		// given: ID는 있으나 실제 DB에 없는 Category
		Category phantom = new Category(null, "유령");
		ReflectionTestUtils.setField(phantom, "id", 9999L);

		// when
		List<Category> result = categoryRepository.getAllDescendantCategories(phantom);

		// then
		assertThat(result).isEmpty();
	}
}
