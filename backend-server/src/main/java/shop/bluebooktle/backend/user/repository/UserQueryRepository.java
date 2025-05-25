package shop.bluebooktle.backend.user.repository;

import java.util.List;

import shop.bluebooktle.common.entity.auth.User;

public interface UserQueryRepository {
	List<User> findByBirthdayMonth(String month);
}