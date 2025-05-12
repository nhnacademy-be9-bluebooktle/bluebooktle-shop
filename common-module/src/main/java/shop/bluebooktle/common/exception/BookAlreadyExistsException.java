package shop.bluebooktle.common.exception;

public class BookAlreadyExistsException extends ApplicationException {
	// public BookAlreadyExistsException(String message) {
	// 	super(message);
	// }

	public BookAlreadyExistsException() {
		super(ErrorCode.BOOK_ALREADY_EXISTS_EXCEPTION);
	}

}
