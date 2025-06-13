package shop.bluebooktle.backend.coupon.batch.birthday;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.entity.auth.User;

@ExtendWith(MockitoExtension.class)
class BirthdayUserItemReaderTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private BirthdayUserItemReader reader;

	@Test
	@DisplayName("read - 생일자가 하나씩 반환")
	void read() {
		// given
		String currentMonth = String.format("%02d", LocalDate.now().getMonthValue());
		User user1 = User.builder().id(1L).loginId("user1").build();
		User user2 = User.builder().id(2L).loginId("user2").build();

		when(userRepository.findByBirthdayMonth(currentMonth)).thenReturn(List.of(user1, user2));

		// when & then
		assertThat(reader.read()).isEqualTo(user1);
		assertThat(reader.read()).isEqualTo(user2);
		assertThat(reader.read()).isNull();
	}

	@Test
	@DisplayName("read - 생일자가 없을 경우 null 반환")
	void read_return_null() {
		// given
		String currentMonth = String.format("%02d", LocalDate.now().getMonthValue());
		when(userRepository.findByBirthdayMonth(currentMonth)).thenReturn(List.of());

		// when
		User result = reader.read();

		// then
		assertThat(result).isNull();
		verify(userRepository).findByBirthdayMonth(currentMonth);
	}
}