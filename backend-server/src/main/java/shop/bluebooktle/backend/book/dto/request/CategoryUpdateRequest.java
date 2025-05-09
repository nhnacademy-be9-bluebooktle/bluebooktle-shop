package shop.bluebooktle.backend.book.dto.request;

import shop.bluebooktle.backend.book.entity.Category;

public record CategoryUpdateRequest(
	Long id,
	String name
) {
	public Category toEntity(Category parentCategory) {
		return new Category(parentCategory, this.name);
	}
}
