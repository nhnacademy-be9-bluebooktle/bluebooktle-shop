package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "tag", url = "${api.baseUrl}/tags")
public interface TagRepository {
}
