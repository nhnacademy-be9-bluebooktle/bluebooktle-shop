package shop.bluebooktle.backend.coupon.batch.direct;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.entity.auth.User;

@Component
@StepScope
@RequiredArgsConstructor
public class DirectCouponReader implements ItemReader<User> {

	private final UserRepository userRepository;
	private Iterator<User> userIterator;

	private static final int PAGE_SIZE = 100;
	private int currentPage = 0;

	@Override
	public User read() {
		if (userIterator == null || !userIterator.hasNext()) {
			PageRequest pageRequest = PageRequest.of(currentPage, PAGE_SIZE);
			List<User> users = userRepository.findByStatus(UserStatus.ACTIVE, pageRequest);

			if (users.isEmpty()) {
				return null;
			}

			userIterator = users.iterator();
			currentPage++;
		}
		return userIterator.hasNext() ? userIterator.next() : null;
	}
}
