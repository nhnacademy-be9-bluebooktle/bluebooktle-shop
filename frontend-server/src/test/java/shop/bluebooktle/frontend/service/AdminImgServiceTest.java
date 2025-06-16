package shop.bluebooktle.frontend.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.frontend.repository.AdminImgRepository;
import shop.bluebooktle.frontend.service.impl.AdminImgServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AdminImgServiceTest {
	@Mock
	AdminImgRepository adminImgRepository;

	@InjectMocks
	AdminImgServiceImpl adminImgService;

	@Test
	@DisplayName("Presigned URL 발급 요청 성공")
	void getPresignedUploadUrl_success() {
		// given
		UUID uuid = UUID.randomUUID();
		String fileName = uuid.toString();
		String presignedUrl = "https://minio.example.com/upload/" + fileName;

		when(adminImgRepository.getPresignedUploadUrl(anyString())).thenReturn(presignedUrl);

		// when
		String result = adminImgService.getPresignedUploadUrl();

		// then
		assertThat(result).isEqualTo(presignedUrl);
		verify(adminImgRepository, times(1)).getPresignedUploadUrl(anyString());
	}

	@Test
	@DisplayName("이미지 삭제 성공")
	void deleteImage_success() {
		// given
		String fileName = "cover/test.jpg";

		// when
		adminImgService.deleteImage(fileName);

		// then
		verify(adminImgRepository, times(1)).deleteImage(fileName);
	}
}
