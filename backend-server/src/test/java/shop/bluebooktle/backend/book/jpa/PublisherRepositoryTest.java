package shop.bluebooktle.backend.book.jpa;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

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
import shop.bluebooktle.backend.book.entity.Publisher;
import shop.bluebooktle.backend.book.repository.PublisherRepository;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.util.CryptoUtils;

// TODO PublisherQueryRepository 테스트코드 구현
@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfiguration.class, CryptoUtils.class,
	ProfileAwareStringCryptoConverter.class})
public class PublisherRepositoryTest {
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
}
