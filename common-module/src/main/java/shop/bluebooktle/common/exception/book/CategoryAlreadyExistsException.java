package shop.bluebooktle.common.exception.book;

public class CategoryAlreadyExistsException extends RuntimeException {
	public CategoryAlreadyExistsException(String name) {
		super(String.format("Category with name %s already exists", name));
	}
}
