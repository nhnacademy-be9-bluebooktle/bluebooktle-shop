package shop.bluebooktle.common.exception;

public class BookAlreadyExistsException extends RuntimeException {
	public BookAlreadyExistsException(String message) {
		super(message);
	}
}
