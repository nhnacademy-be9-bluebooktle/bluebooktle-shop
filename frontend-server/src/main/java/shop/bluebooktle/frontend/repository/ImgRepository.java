package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(name = "backend-server", contextId = "imgRepository", path = "/api/imgs", configuration = FeignGlobalConfig.class)
public interface ImgRepository {

	// 이미지 아이디로 삭제
	@DeleteMapping("{imageId}")
	Void deleteImg(
		@PathVariable Long imageId
	);

	@GetMapping("/by-review/{reviewId}")
	ImgResponse getImageByReviewId(
		@PathVariable("reviewId") Long reviewId
	);
}
