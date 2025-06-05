package shop.bluebooktle.backend.user.batch.dormant;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.entity.auth.User;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class DormantUserItemReader implements ItemReader<User> {

	private final UserRepository userRepository;
	private Iterator<User> userIterator;
	private static final int MONTHS_TO_BECOME_DORMANT = 3;

	@Override
	public User read() {
		if (userIterator == null) {
			LocalDateTime cutoffDateTime = LocalDateTime.now().minusMonths(MONTHS_TO_BECOME_DORMANT);
			List<User> usersToBecomeDormant = userRepository.findByStatusAndLastLoginAtBefore(UserStatus.ACTIVE,
				cutoffDateTime);
			log.info("휴면 대상 사용자 {}명", usersToBecomeDormant.size());
			userIterator = usersToBecomeDormant.iterator();
		}

		return userIterator.hasNext() ? userIterator.next() : null;
	}
}