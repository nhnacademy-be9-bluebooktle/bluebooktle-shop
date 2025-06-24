package shop.bluebooktle.backend.coupon.batch.direct;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageRequest;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.entity.auth.User;

@ExtendWith(MockitoExtension.class)
class DirectCouponReaderTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private DirectCouponReader directCouponReader;

	@Test
	@DisplayName("read - 유저 하나씩 반환")
	void read() {
		// given
		User user1 = User.builder().id(1L).loginId("user1").build();
		User user2 = User.builder().id(2L).loginId("user2").build();
		when(userRepository.findByStatus(eq(UserStatus.ACTIVE), any(PageRequest.class)))
				.thenReturn(List.of(user1, user2))
				.thenReturn(List.of());

		// when
		User result1 = directCouponReader.read();
		User result2 = directCouponReader.read();
		User result3 = directCouponReader.read();

		// then
		assertThat(result1).isEqualTo(user1);
		assertThat(result2).isEqualTo(user2);
		assertThat(result3).isNull();
		verify(userRepository, times(2)).findByStatus(eq(UserStatus.ACTIVE), any(PageRequest.class));
	}

	@Test
	@DisplayName("read - 없을 경우 null")
	void read_return_null() {
		// given
		when(userRepository.findByStatus(eq(UserStatus.ACTIVE), any(PageRequest.class)))
				.thenReturn(List.of());

		// when
		User result = directCouponReader.read();

		// then
		assertThat(result).isNull();
		verify(userRepository).findByStatus(eq(UserStatus.ACTIVE), any(PageRequest.class));
	}
}