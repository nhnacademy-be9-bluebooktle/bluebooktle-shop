package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.book.response.BookLikesResponse;

class BookLikesReponseTest {

	@Test
	@DisplayName("Builder로 객체 생성 및 값 확인")
	void builderTest() {
		// given
		Long bookId = 10L;
		boolean isLiked = true;
		int countLikes = 123;

		// when
		BookLikesResponse response = BookLikesResponse.builder()
			.bookId(bookId)
			.isLiked(isLiked)
			.countLikes(countLikes)
			.build();

		// then
		assertThat(response.getBookId()).isEqualTo(bookId);
		assertThat(response.isLiked()).isTrue();
		assertThat(response.getCountLikes()).isEqualTo(countLikes);
	}

	@Test
	@DisplayName("기본 생성자 + setter 테스트")
	void noArgsConstructorTest() {
		// given
		BookLikesResponse response = new BookLikesResponse();

		// when
		response.setBookId(42L);
		response.setLiked(false);
		response.setCountLikes(999);

		// then
		assertThat(response.getBookId()).isEqualTo(42L);
		assertThat(response.isLiked()).isFalse();
		assertThat(response.getCountLikes()).isEqualTo(999);
	}

}
