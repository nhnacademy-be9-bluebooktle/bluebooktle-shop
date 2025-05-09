package shop.bluebooktle.common.exception.user;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(String message) {
		super("User not found with id, " + message);
	}
}
