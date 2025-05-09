package shop.bluebooktle.common.exception.book;

public class CategoryNotFoundException extends RuntimeException {
	public CategoryNotFoundException(Long id) {
		super(String.format("Category with id %d does not exist", id));
	}
}
