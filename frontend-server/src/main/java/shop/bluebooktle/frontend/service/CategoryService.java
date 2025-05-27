package shop.bluebooktle.frontend.service;

import java.util.List;

import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;

public interface CategoryService {
	List<CategoryTreeResponse> getCategoryTreeCached();
}
