package shop.bluebooktle.backend.book_order.jpa;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfiguration.class})
class PackagingOptionRepositoryTest {

	@Autowired
	private PackagingOptionRepository repository;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("저장된 이름의 존재 여부 - 성공")
	void existsByName_true() {
		// given
		PackagingOption opt = PackagingOption.builder()
			.name("Gift Wrap")
			.price(BigDecimal.valueOf(500))
			.build();
		em.persist(opt);
		em.flush();
		em.clear();

		// when & then
		assertThat(repository.existsByName("Gift Wrap")).isTrue();
	}

	@Test
	@DisplayName("저장되지 않은 이름의 존재 여부 - 실패")
	void existsByName_false() {
		assertThat(repository.existsByName("Nonexistent")).isFalse();
	}
}
