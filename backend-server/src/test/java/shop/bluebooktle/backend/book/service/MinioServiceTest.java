package shop.bluebooktle.backend.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import shop.bluebooktle.backend.book.service.impl.MinioServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

	@Mock
	private MinioClient minioClient;

	@InjectMocks
	private MinioServiceImpl minioService;

	private static final String BUCKET = "test-bucket";

	@BeforeEach
	void setUp() {
		// @Value 로 주입되는 필드에 테스트용 버킷명 설정
		ReflectionTestUtils.setField(minioService, "bucketName", BUCKET);
	}

	@Test
	@DisplayName("getPresignedUploadUrl - 성공")
	void getPresignedUploadUrl_success() throws Exception {
		String fileName = "foo.png";
		String expectedUrl = "https://minio.test/upload/foo.png";

		when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
			.thenReturn(expectedUrl);

		String url = minioService.getPresignedUploadUrl(fileName);

		// 리퀘스트 인자 검증
		verify(minioClient).getPresignedObjectUrl(argThat(args ->
			BUCKET.equals(args.bucket()) &&
				fileName.equals(args.object()) &&
				args.method() == Method.PUT &&
				args.expiry() == 60 * 60
		));
		assertThat(url).isEqualTo(expectedUrl);
	}

	@Test
	@DisplayName("getPresignedUploadUrl - 실패 시 RuntimeException")
	void getPresignedUploadUrl_failure() throws Exception {
		String fileName = "bar.jpg";
		when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
			.thenThrow(new IllegalStateException("oops"));

		assertThatThrownBy(() -> minioService.getPresignedUploadUrl(fileName))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Presigned URL 생성 실패")
			.hasCauseInstanceOf(IllegalStateException.class);
	}

	@Test
	@DisplayName("deleteImage - 성공")
	void deleteImage_success() throws Exception {
		String fileName = "to-delete.png";

		// removeObject 는 기본적으로 void 이므로 아무 예외도 던지지 않게 두면 성공 경로
		minioService.deleteImage(fileName);

		verify(minioClient).removeObject(argThat(args ->
			BUCKET.equals(args.bucket()) &&
				fileName.equals(args.object())
		));
	}

	@Test
	@DisplayName("deleteImage - 실패 시 RuntimeException")
	void deleteImage_failure() throws Exception {
		String fileName = "cannot-delete.png";
		doThrow(new RuntimeException("remove error"))
			.when(minioClient).removeObject(any(RemoveObjectArgs.class));

		assertThatThrownBy(() -> minioService.deleteImage(fileName))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("미니오 삭제 실패")
			.hasCauseInstanceOf(RuntimeException.class);
	}
}
