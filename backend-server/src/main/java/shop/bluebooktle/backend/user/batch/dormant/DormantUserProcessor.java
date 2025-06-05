package shop.bluebooktle.backend.user.batch.dormant;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.entity.auth.User;

@Component
@Slf4j
public class DormantUserProcessor implements ItemProcessor<User, User> {

	@Override
	public User process(User user) {
		user.setStatus(UserStatus.DORMANT);
		return user;
	}
}