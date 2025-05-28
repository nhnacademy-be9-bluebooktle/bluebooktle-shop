package shop.bluebooktle.backend.book_order.jpa;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfiguration.class})
class PackagingOptionQueryRepositoryTest {

	@Autowired
	private PackagingOptionRepository packagingOptionRepository;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("키워드 없이 옵션 조회 - 성공")
	void searchNameContaining_withoutKeyword() {
		// given
		packagingOptionRepository.save(PackagingOption.builder()
			.name("도트 포장지")
			.price(BigDecimal.valueOf(100)).build());
		packagingOptionRepository.save(PackagingOption.builder()
			.name("체크 포장지")
			.price(BigDecimal.valueOf(200)).build());
		packagingOptionRepository.save(PackagingOption
			.builder().name("리본 포장지")
			.price(BigDecimal.valueOf(300)).build());
		em.flush();
		em.clear();

		// when
		Page<PackagingOption> page = packagingOptionRepository.searchNameContaining(
			null, PageRequest.of(0, 10)
		);

		// then
		assertThat(page.getTotalElements()).isEqualTo(3);
		List<String> names = page.map(PackagingOption::getName).toList();
		assertThat(names).containsExactly("도트 포장지", "리본 포장지", "체크 포장지");
	}

	@Test
	@DisplayName("삭제된 옵션 제외 조회 - 성공")
	void searchNameContaining_afterDelete() {
		// given
		packagingOptionRepository.save(
			PackagingOption.builder()
				.name("기본 포장지")
				.price(BigDecimal.valueOf(100))
				.build()
		);
		PackagingOption toDelete = packagingOptionRepository.save(
			PackagingOption.builder()
				.name("삭제된 포장지")
				.price(BigDecimal.valueOf(200))
				.build()
		);
		em.flush();

		packagingOptionRepository.delete(toDelete);
		em.flush();
		em.clear();

		// when
		Page<PackagingOption> page = packagingOptionRepository.searchNameContaining(
			null, PageRequest.of(0, 10)
		);

		// then
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getName()).isEqualTo("기본 포장지");
	}
}
