package shop.bluebooktle.common.dto.book.response;

import java.util.ArrayList;
import java.util.List;

public record CategoryTreeResponse(
	Long id,
	String name,
	List<CategoryTreeResponse> children
) {
	public CategoryTreeResponse(Long id, String name) {
		this(id, name, new ArrayList<>());
	}
}
