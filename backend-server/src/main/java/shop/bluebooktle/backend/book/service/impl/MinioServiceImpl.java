package shop.bluebooktle.backend.book.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.service.MinioService;

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

	private final MinioClient minioClient;

	@Value("${minio.bucket}")
	private String bucketName;

	/**
	 * 업로드된 이미지 파일에 대한 Presigned URL을 생성
	 */
	public String getPresignedUploadUrl(String fileName) {
		try {
			return minioClient.getPresignedObjectUrl(
				GetPresignedObjectUrlArgs.builder()
					.bucket(bucketName)
					.object(fileName)
					.method(Method.PUT)
					.expiry(60 * 60) // 1시간 동안 유효 - 수정필요
					.build()
			);
		} catch (Exception e) {
			throw new RuntimeException("Presigned URL 생성 실패: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteMinioBucket(String fileName) {
		try {
			minioClient.removeObject(
				RemoveObjectArgs.builder()
					.bucket(bucketName)
					.object(fileName)
					.build()
			);
		} catch (Exception e) {
			throw new RuntimeException("미니오 삭제 실패: " + e.getMessage(), e);
		}
	}
}