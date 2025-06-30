package shop.bluebooktle.backend.coupon.batch.birthday;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.entity.auth.User;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class BirthdayUserItemReader implements ItemReader<User> {

	private final UserRepository userRepository;

	private static final int PAGE_SIZE = 100;

	private int currentPage = 0;
	private Iterator<User> userIterator;

	@Override
	public User read() {
		if (userIterator == null || !userIterator.hasNext()) {
			String month = String.format("%02d", LocalDate.now().getMonthValue());
			List<User> users = userRepository.findByBirthdayMonth(month, currentPage, PAGE_SIZE);

			if (users.isEmpty()) {
				return null;
			}

			log.info("Reader : 생일자 {}월 유저 수 : {}", month, users.size());
			userIterator = users.iterator();
			currentPage++;
		}
		return userIterator.hasNext() ? userIterator.next() : null;
	}
}