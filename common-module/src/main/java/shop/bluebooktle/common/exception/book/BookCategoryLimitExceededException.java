package shop.bluebooktle.common.exception.book;

public class BookCategoryLimitExceededException extends RuntimeException {
	public BookCategoryLimitExceededException(Long bookId) {
		super("Book(id=" + bookId + ") can have at most 10 categories.");
	}
}
