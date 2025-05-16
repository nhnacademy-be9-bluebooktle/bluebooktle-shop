package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "backend-server", url = "/tags")
public interface TagRepository {
}
