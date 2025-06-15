package shop.bluebooktle.backend.elasticsearch.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Field;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@ExtendWith(MockitoExtension.class)
class ElasticsearchConfigTest {

	@Mock
	ElasticsearchClient elasticsearchClient;

	@Mock
	ElasticsearchTransport transport;

	@Mock
	JsonpMapper jsonpMapper;

	@InjectMocks
	ElasticsearchConfig config;

	@BeforeEach
	void setUp() throws Exception {
		Field hostField = ElasticsearchConfig.class.getDeclaredField("host");
		hostField.setAccessible(true);
		hostField.set(config, "localhost:9200");

		Field usernameField = ElasticsearchConfig.class.getDeclaredField("username");
		usernameField.setAccessible(true);
		usernameField.set(config, "elastic");

		Field passwordField = ElasticsearchConfig.class.getDeclaredField("password");
		passwordField.setAccessible(true);
		passwordField.set(config, "changeme");

	}

	@Test
	@DisplayName("clientConfiguration() 직접 호출")
	void clientConfiguration_direct_call() {
		assertThat(config.clientConfiguration()).isNotNull();
	}

	@Test
	@DisplayName("elasticsearchOperations() 직접 호출")
	void elasticsearchOperationsBean_register_success() {
		given(elasticsearchClient._transport()).willReturn(transport);
		given(transport.jsonpMapper()).willReturn(jsonpMapper);

		ElasticsearchOperations operations = config.elasticsearchOperations(elasticsearchClient);
		assertThat(operations).isInstanceOf(ElasticsearchTemplate.class);
	}
}
