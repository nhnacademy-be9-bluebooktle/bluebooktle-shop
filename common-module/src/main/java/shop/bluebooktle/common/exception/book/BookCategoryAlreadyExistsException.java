package shop.bluebooktle.common.exception.book;

public class BookCategoryAlreadyExistsException extends RuntimeException {
	public BookCategoryAlreadyExistsException(Long bookId, Long categoryId) {
		super("Book category with id " + bookId + " already exists in category " + categoryId);
	}
}
