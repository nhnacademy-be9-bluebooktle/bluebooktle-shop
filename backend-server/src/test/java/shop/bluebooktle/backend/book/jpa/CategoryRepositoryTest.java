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

import jakarta.persistence.EntityManager;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfiguration.class})
public class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private EntityManager em;

	private Category category1;
	private Category category2;
	private Category category3;
	private Category category4;

	@BeforeEach
	void setUp() {
		category1 = categoryRepository.save(new Category(null, "소설"));
		category2 = categoryRepository.save(new Category(null, "일반 소설"));
		category3 = categoryRepository.save(new Category(null, "장르 소설"));

		em.persist(category1);
		em.persist(category2);
		em.persist(category3);

		category4 = categoryRepository.save(new Category(null, "국내 현대 소설"));
		em.persist(category4);
		em.flush();

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
		assertThat(names).containsExactly("소설", "일반 소설", "장르 소설");
		assertThat(names).doesNotContain("국내 현대 소설");
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

}
