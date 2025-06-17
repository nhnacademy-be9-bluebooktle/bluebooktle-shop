package shop.bluebooktle.backend.book.jpa;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

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
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import shop.bluebooktle.backend.book.entity.Tag;
import shop.bluebooktle.backend.book.repository.TagRepository;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.util.CryptoUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfiguration.class, CryptoUtils.class,
	ProfileAwareStringCryptoConverter.class})
class TagRepositoryTest {

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private EntityManager em;

	@BeforeEach
	void setUp() {
		em.persist(Tag.builder().name("Java").build());
		em.persist(Tag.builder().name("Spring").build());
		em.persist(Tag.builder().name("JPA").build());

		Tag deletedTag = Tag.builder().name("Javascript").build();
		em.persist(deletedTag);
		em.flush();

		em.createQuery("update Tag t set t.deletedAt = :now where t.name = :name")
			.setParameter("now", LocalDateTime.now())
			.setParameter("name", "Javascript")
			.executeUpdate();
		em.clear();
	}

	@Test
	@DisplayName("이름에 키워드가 포함된 태그 검색 - 삭제되지 않은 항목만")
	void searchByNameContaining() {
		Page<Tag> result = tagRepository.searchByNameContaining("ja", PageRequest.of(0, 10));
		List<String> names = result.getContent().stream().map(Tag::getName).toList();
		assertThat(names).containsExactly("Java");
		assertThat(names).doesNotContain("Javascript");
		assertThat(result.getTotalElements()).isEqualTo(1);
	}

	@Test
	@DisplayName("빈 검색어 전달 시 전체 반환 (삭제 제외)")
	void searchWithEmptyKeyword() {
		Page<Tag> result = tagRepository.searchByNameContaining("", PageRequest.of(0, 10));
		assertThat(result.getTotalElements()).isEqualTo(3);
		assertThat(result.getContent())
			.extracting(Tag::getName)
			.doesNotContain("Javascript");
	}

	@Test
	@DisplayName("페이징 동작 확인")
	void searchWithPaging() {
		Page<Tag> page1 = tagRepository.searchByNameContaining("", PageRequest.of(0, 2));
		Page<Tag> page2 = tagRepository.searchByNameContaining("", PageRequest.of(1, 2));

		List<String> combined = Stream.concat(
			page1.getContent().stream(),
			page2.getContent().stream()
		).map(Tag::getName).toList();

		assertThat(combined).containsExactlyInAnyOrder("Java", "Spring", "JPA");
	}
}