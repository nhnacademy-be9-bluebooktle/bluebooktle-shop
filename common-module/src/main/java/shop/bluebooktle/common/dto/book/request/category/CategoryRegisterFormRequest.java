package shop.bluebooktle.common.dto.book.request.category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRegisterFormRequest {
	private Long id;
	private String rootCategoryName;
	private String childCategoryName;
	private String name;
}
