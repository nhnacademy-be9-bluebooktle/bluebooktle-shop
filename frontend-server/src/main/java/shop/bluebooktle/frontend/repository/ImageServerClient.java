package shop.bluebooktle.frontend.repository;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
	name = "minioClient",
	url = "${minio.endpoint}"
)
public interface ImageServerClient {
	/**
	 * @param bucket     버킷 이름 (여기선 "bluebooktle-bookimage")
	 * @param objectName 오브젝트 키 (여기선 "86bb07aa-…")
	 * @param payload    파일 바이트
	 */
	@PutMapping(
		value = "/{bucket}/{objectName}"
	)
	void upload(
		@PathVariable("bucket") String bucket,
		@PathVariable("objectName") String objectName,
		@RequestParam Map<String, String> queryParams, // ③ 쿼리스트링
		// @RequestHeader("Content-Type") String contentType,
		@RequestHeader Map<String, String> headers,    // ← 헤더 맵
		@RequestBody byte[] payload
	);

	@GetMapping("/{bucket}/{objectName}")
	byte[] download(
		@PathVariable("bucket") String bucket,
		@PathVariable("objectName") String objectName);
}