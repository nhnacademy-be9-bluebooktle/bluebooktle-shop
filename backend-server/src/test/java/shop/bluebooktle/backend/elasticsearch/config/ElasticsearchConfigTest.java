package shop.bluebooktle.backend.elasticsearch.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@SpringBootTest
class ElasticsearchConfigTest {

	@Autowired
	ElasticsearchOperations elasticsearchOperations;

	@Test
	@DisplayName("ElasticsearchOperations 빈 정상 로드")
	void elasticsearchOperationsBeanLoads() {
		assertThat(elasticsearchOperations).isNotNull();
	}
}
