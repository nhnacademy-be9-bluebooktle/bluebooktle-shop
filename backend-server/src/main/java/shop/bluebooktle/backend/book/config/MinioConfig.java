package shop.bluebooktle.backend.book.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

//이미지서버 minio
@Configuration
@RefreshScope
public class MinioConfig {

	@Value("${minio.endpoint}")
	private String endpoint;

	@Value("${minio.access-key}")
	private String accessKey;

	@Value("${minio.secret-key}")
	private String secretKey;

	@Bean
	public MinioClient minioClient() {
		return MinioClient.builder()
			.endpoint(endpoint)
			.credentials(accessKey, secretKey)
			.build();
	}
}
