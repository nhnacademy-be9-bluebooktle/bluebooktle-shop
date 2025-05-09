package shop.bluebooktle.common.exception.book;

public class BookNotFoundException extends RuntimeException {
	public BookNotFoundException(String message) {
		super("Book not found with id, " + message);
	}
}
