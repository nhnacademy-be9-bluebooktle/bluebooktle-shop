package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.common.dto.book.response.AladinAuthorResponse;

class AladinAuthorResponseTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	@DisplayName("정상 필드 매핑 테스트")
	void fieldMappingTest() throws Exception {
		// given
		String json = """
			{
			  "authorId": 123,
			  "authorName": "홍길동"
			}
			""";

		// when
		AladinAuthorResponse response = objectMapper.readValue(json, AladinAuthorResponse.class);

		// then
		assertThat(response.getAuthorId()).isEqualTo(123);
		assertThat(response.getAuthorName()).isEqualTo("홍길동");
	}

	@Test
	@DisplayName("알 수 없는 필드 무시 테스트")
	void ignoreUnknownPropertiesTest() throws Exception {
		// given
		String json = """
			{
			  "authorId": 456,
			  "authorName": "이몽룡",
			  "unknownField": "무시됩니다"
			}
			""";

		// when
		AladinAuthorResponse response = objectMapper.readValue(json, AladinAuthorResponse.class);

		// then
		assertThat(response.getAuthorId()).isEqualTo(456);
		assertThat(response.getAuthorName()).isEqualTo("이몽룡");
	}

}
