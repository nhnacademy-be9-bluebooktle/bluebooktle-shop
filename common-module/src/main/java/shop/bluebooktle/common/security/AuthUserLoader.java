package shop.bluebooktle.common.security;

import shop.bluebooktle.common.exception.auth.UserNotFoundException;

public interface AuthUserLoader {
	UserPrincipal loadUserById(Long userId) throws UserNotFoundException;
}