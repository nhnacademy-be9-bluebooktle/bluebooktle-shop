package shop.bluebooktle.common.service;

import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.principal.UserPrincipal;

public interface AuthUserLoader {
	UserPrincipal loadUserById(Long userId) throws UserNotFoundException;
}