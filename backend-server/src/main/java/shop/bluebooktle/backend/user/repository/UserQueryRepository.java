package shop.bluebooktle.backend.user.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.user.request.UserSearchRequest;
import shop.bluebooktle.common.entity.auth.User;

public interface UserQueryRepository {
	List<User> findByBirthdayMonth(String month);

	Page<User> findUsersBySearchRequest(UserSearchRequest request, Pageable pageable);

}