package shop.bluebooktle.common.exception.book;

public class BookCategoryNotFoundException extends RuntimeException {
	public BookCategoryNotFoundException(Long bookId, Long categoryId) {
		super("Book category with id " + bookId + " does not exist in category " + categoryId);
	}
}
