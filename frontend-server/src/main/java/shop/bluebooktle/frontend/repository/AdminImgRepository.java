package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(name = "backend-server", contextId = "adminImgRepository", path = "/api/imgs", configuration = FeignGlobalConfig.class)
public interface AdminImgRepository {
	@GetMapping("/presignedUploadUrl")
	String getPresignedUploadUrl(@RequestParam("fileName") String fileName);
}
