package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import shop.bluebooktle.common.dto.common.PaginationData;

class PaginationDataTest {

	@Test
	@DisplayName("Page 객체 기반 PaginationData 생성 테스트")
	void createPaginationDataFromPage() {
		// given
		List<String> content = List.of("A", "B", "C");
		int page = 1;
		int size = 3;
		long totalElements = 10;

		Page<String> mockPage = new PageImpl<>(content, PageRequest.of(page, size), totalElements);

		// when
		PaginationData<String> paginationData = new PaginationData<>(mockPage);

		// then
		assertThat(paginationData.getContent()).containsExactly("A", "B", "C");

		PaginationData.PaginationInfo info = paginationData.getPagination();
		assertThat(info.getTotalPages()).isEqualTo((int)Math.ceil((double)totalElements / size));
		assertThat(info.getTotalElements()).isEqualTo(totalElements);
		assertThat(info.getCurrentPage()).isEqualTo(page);
		assertThat(info.getPageSize()).isEqualTo(size);
		assertThat(info.isFirst()).isFalse();
		assertThat(info.isLast()).isFalse();
	}

	@Test
	@DisplayName("PaginationInfo @JsonCreator 생성자 테스트")
	void jsonCreatorConstructorTest() {
		// given
		PaginationData.PaginationInfo info = new PaginationData.PaginationInfo(
			5, 100L, 2, 20, false, false, true, true
		);

		// then
		assertThat(info.getTotalPages()).isEqualTo(5);
		assertThat(info.getTotalElements()).isEqualTo(100L);
		assertThat(info.getCurrentPage()).isEqualTo(2);
		assertThat(info.getPageSize()).isEqualTo(20);
		assertThat(info.isFirst()).isFalse();
		assertThat(info.isLast()).isFalse();
	}

	@Test
	@DisplayName("PaginationData.getTotalElements() 위임 테스트")
	void getTotalElementsTest() {
		// given
		PaginationData.PaginationInfo info = new PaginationData.PaginationInfo(
			3, 45L, 0, 15, true, false, true, false
		);
		PaginationData<String> data = new PaginationData<>();
		data.setPagination(info);

		// when
		long total = data.getTotalElements();

		// then
		assertThat(total).isEqualTo(45L);
	}

}
