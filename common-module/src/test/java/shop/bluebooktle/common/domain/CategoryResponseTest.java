package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.book.response.CategoryResponse;

class CategoryResponseTest {

	@Test
	@DisplayName("CategoryResponse 빌더 생성 및 필드 값 검증")
	void builderTest() {
		// given
		Long categoryId = 101L;
		String name = "국내도서";
		String parentName = "도서";
		String categoryPath = "/1/101";

		// when
		CategoryResponse response = CategoryResponse.builder()
			.categoryId(categoryId)
			.name(name)
			.parentName(parentName)
			.categoryPath(categoryPath)
			.build();

		// then
		assertThat(response.categoryId()).isEqualTo(categoryId);
		assertThat(response.name()).isEqualTo(name);
		assertThat(response.parentName()).isEqualTo(parentName);
		assertThat(response.categoryPath()).isEqualTo(categoryPath);
	}

}
