package shop.bluebooktle.frontend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.frontend.repository.ImageServerClient;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
	private final ImageServerClient imageServerClient;

	@GetMapping("/{bucket}/{objectName}")
	public ResponseEntity<byte[]> proxyPngImage(@PathVariable(name = "bucket") String bucket,
		@PathVariable(name = "objectName") String objectName) {
		try {
			byte[] imageData = imageServerClient.download(bucket, objectName);
			if (imageData == null || imageData.length == 0) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok()
				.contentType(MediaType.IMAGE_PNG)
				.body(imageData);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
