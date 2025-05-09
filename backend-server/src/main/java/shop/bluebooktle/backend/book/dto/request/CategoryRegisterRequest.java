package shop.bluebooktle.backend.book.dto.request;

import shop.bluebooktle.backend.book.entity.Category;

public record CategoryRegisterRequest(
	String name,
	Long parentCategoryId
) {
	public Category toEntity(Category parentCategory) {
		return new Category(parentCategory, this.name);
	}
}
