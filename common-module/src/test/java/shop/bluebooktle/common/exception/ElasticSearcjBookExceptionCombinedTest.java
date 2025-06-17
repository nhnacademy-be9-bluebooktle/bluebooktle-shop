package shop.bluebooktle.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.exception.elasticsearch.ElasticsearchBookDocumentSaveException;
import shop.bluebooktle.common.exception.elasticsearch.ElasticsearchBookNotFoundException;

class ElasticSearcjBookExceptionCombinedTest {

	@Test
	@DisplayName("ElasticsearchBookDocumentSaveException 테스트")
	void elasticsearchBookDocumentSaveException() {
		String message = "Elasticsearch 저장 실패";
		ElasticsearchBookDocumentSaveException exception = new ElasticsearchBookDocumentSaveException(message);

		assertThat(exception)
			.isInstanceOf(ElasticsearchBookDocumentSaveException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.ELASTIC_SEARCH_SAVE_ERROR);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("ElasticsearchBookNotFoundException 테스트")
	void elasticsearchBookNotFoundException() {
		ElasticsearchBookNotFoundException exception = new ElasticsearchBookNotFoundException();

		assertThat(exception)
			.isInstanceOf(ElasticsearchBookNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.ELASTIC_SEARCH_SAVE_ERROR);
	}

}
