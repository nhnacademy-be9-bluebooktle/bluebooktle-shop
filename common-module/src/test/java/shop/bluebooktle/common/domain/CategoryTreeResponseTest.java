package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;

class CategoryTreeResponseTest {

	@Test
	@DisplayName("기본 생성자 - children 자동 초기화 확인")
	void constructorWithIdAndName() {
		// given
		Long id = 1L;
		String name = "도서";

		// when
		CategoryTreeResponse response = new CategoryTreeResponse(id, name);

		// then
		assertThat(response.id()).isEqualTo(id);
		assertThat(response.name()).isEqualTo(name);
	}

	@Test
	@DisplayName("전체 생성자 - children 포함 트리 구조 확인")
	void constructorWithChildren() {
		// given
		CategoryTreeResponse child1 = new CategoryTreeResponse(2L, "국내도서");
		CategoryTreeResponse child2 = new CategoryTreeResponse(3L, "해외도서");
		List<CategoryTreeResponse> children = List.of(child1, child2);

		// when
		CategoryTreeResponse parent = new CategoryTreeResponse(1L, "도서", children);

		// then
		assertThat(parent.id()).isEqualTo(1L);
		assertThat(parent.name()).isEqualTo("도서");
	}

}
