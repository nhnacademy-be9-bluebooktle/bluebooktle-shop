package shop.bluebooktle.common.exception.book;

public class BookMustHaveAtLeastOneCategoryException extends RuntimeException {
	public BookMustHaveAtLeastOneCategoryException(Long bookId) {
		super("Book (id=" + bookId + ") must belong to at least one category.");
	}
}
