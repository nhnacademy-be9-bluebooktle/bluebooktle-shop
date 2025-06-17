package shop.bluebooktle.common.dto.book.response;

import lombok.Builder;

@Builder
public record CategoryResponse(
	Long categoryId,
	String name,
	String parentName,
	String categoryPath
) {
}