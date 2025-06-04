package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(url = "${server.gateway-url}", name = "bookImgRepository", path = "/api/books", configuration = FeignGlobalConfig.class)
public interface BookImgRepository {

	@GetMapping("/{bookId}/images")
	ImgResponse getImgsByBookId(@PathVariable Long bookId);

}
