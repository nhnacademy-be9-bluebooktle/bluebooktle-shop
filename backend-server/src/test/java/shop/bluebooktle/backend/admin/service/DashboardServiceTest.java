package shop.bluebooktle.backend.admin.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.admin.DashboardStatusResponse;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private DashboardService dashboardService;


	@Test
	@DisplayName("관리자 대시보드 상태 조회")
	void getDashboardStatus_success() {
		// given
		long totalUsers = 100L;
		long todayOrders = 5L;
		long preparingOrders = 2L;

		LocalDateTime todayStart = LocalDate.now().atStartOfDay();
		LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

		when(userRepository.count()).thenReturn(totalUsers);
		when(orderRepository.countByCreatedAtBetween(todayStart, todayEnd)).thenReturn(todayOrders);
		when(orderRepository.countByOrderState_State(OrderStatus.PREPARING)).thenReturn(preparingOrders);

		// when
		DashboardStatusResponse result = dashboardService.getDashboardStatus();

		// then
		assertThat(result.totalUserCount()).isEqualTo(totalUsers);
		assertThat(result.todayOrderCount()).isEqualTo(todayOrders);
		assertThat(result.pendingOrderCount()).isEqualTo(preparingOrders);

		verify(userRepository).count();
		verify(orderRepository).countByCreatedAtBetween(todayStart, todayEnd);
		verify(orderRepository).countByOrderState_State(OrderStatus.PREPARING);
	}
}