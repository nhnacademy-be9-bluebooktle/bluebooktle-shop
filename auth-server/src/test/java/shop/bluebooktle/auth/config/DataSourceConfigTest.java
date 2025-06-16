package shop.bluebooktle.auth.config;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DataSourceConfigTest {

	private DataSourceConfig dataSourceConfig;

	@BeforeEach
	void setup() {
		dataSourceConfig = new DataSourceConfig();

		// private 필드 주입
		ReflectionTestUtils.setField(dataSourceConfig, "driverClassName", "com.mysql.cj.jdbc.Driver");
		ReflectionTestUtils.setField(dataSourceConfig, "url", "jdbc:mysql://localhost:3306/test_db");
		ReflectionTestUtils.setField(dataSourceConfig, "username", "testuser");
		ReflectionTestUtils.setField(dataSourceConfig, "password", "testpass");
	}

	@Test
	@DisplayName("DataSource 생성 및 설정값 검증")
	void dataSource_bean_created_correctly() {
		BasicDataSource dataSource = (BasicDataSource)dataSourceConfig.dataSource();

		assertThat(dataSource.getDriverClassName()).isEqualTo("com.mysql.cj.jdbc.Driver");
		assertThat(dataSource.getUrl()).isEqualTo("jdbc:mysql://localhost:3306/test_db");

		assertThat(dataSource.getUsername()).isEqualTo("testuser");
		assertThat(dataSource.getPassword()).isEqualTo("testpass");

		assertThat(dataSource.getInitialSize()).isEqualTo(10);
		assertThat(dataSource.getMaxTotal()).isEqualTo(10);
		assertThat(dataSource.getMaxIdle()).isEqualTo(10);
		assertThat(dataSource.getMinIdle()).isEqualTo(10);
		assertThat(dataSource.getMaxWaitDuration()).isEqualTo(Duration.ofMillis(3000));

		assertThat(dataSource.getValidationQuery()).isEqualTo("SELECT 1");
		assertThat(dataSource.getTestOnBorrow()).isTrue();
		assertThat(dataSource.getTestWhileIdle()).isFalse();
	}
}
