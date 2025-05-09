package shop.bluebooktle.common.exception.book;

public class BookLikesAlreadyChecked extends RuntimeException {
	public BookLikesAlreadyChecked(String message) {
		super("Already checked book likes with bookId, " + message);
	}
}
