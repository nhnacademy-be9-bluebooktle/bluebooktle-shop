package shop.bluebooktle.backend.coupon.batch.direct;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
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

	@Override
	public User read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (userIterator == null) {
			List<User> users = userRepository.findByStatus(UserStatus.ACTIVE);
			userIterator = users.iterator();
		}
		return userIterator.hasNext() ? userIterator.next() : null;
	}
}
