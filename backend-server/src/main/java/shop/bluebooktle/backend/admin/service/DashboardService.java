package shop.bluebooktle.backend.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.admin.DashboardStatusResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

	private final UserRepository userRepository;
	private final OrderRepository orderRepository;

	public DashboardStatusResponse getDashboardStatus() {

		long totalUserCount = userRepository.count();

		LocalDateTime todayStart = LocalDate.now().atStartOfDay();
		LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
		long todayOrderCount = orderRepository.countByCreatedAtBetween(todayStart, todayEnd);

		long pendingOrderCount = orderRepository.countByOrderState_State(OrderStatus.PREPARING); //

		return new DashboardStatusResponse(totalUserCount, todayOrderCount, pendingOrderCount);
	}
}