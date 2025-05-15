package shop.bluebooktle.backend.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.order.dto.response.OrderStateResponse;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.backend.order.repository.OrderStateRepository;
import shop.bluebooktle.backend.order.service.impl.OrderStateServiceImpl;
import shop.bluebooktle.common.domain.OrderStatus;

@ExtendWith(MockitoExtension.class)
class OrderStateServiceTest {

	@InjectMocks
	private OrderStateServiceImpl orderStateService;

	@Mock
	private OrderStateRepository orderStateRepository;

	@Test
	@DisplayName("모든 주문 상태 조회 - 성공")
	void getAll_success() {
		// given
		List<OrderState> orderStates = List.of(
			new OrderState(OrderStatus.PENDING),
			new OrderState(OrderStatus.SHIPPING)
		);
		given(orderStateRepository.findAll()).willReturn(orderStates);

		// when
		List<OrderState> result = orderStateService.getAll();

		// then
		assertThat(result).hasSize(2);
		verify(orderStateRepository).findAll();
	}

	@Test
	@DisplayName("주문 상태로 조회 - 성공")
	void getByState_success() {
		// given
		OrderState pending = new OrderState(OrderStatus.PENDING);
		given(orderStateRepository.findByState(OrderStatus.PENDING)).willReturn(Optional.of(pending));

		// when
		Optional<OrderStateResponse> result = orderStateService.getByState(OrderStatus.PENDING);

		// then
		assertThat(result).isPresent();
		assertThat(result.get().state()).isEqualTo(OrderStatus.PENDING);
		verify(orderStateRepository).findByState(OrderStatus.PENDING);
	}

	@Test
	@DisplayName("주문 상태로 조회 - 존재하지 않음")
	void getByState_notFound() {
		// given
		given(orderStateRepository.findByState(OrderStatus.RETURNED)).willReturn(Optional.empty());

		// when
		Optional<OrderStateResponse> result = orderStateService.getByState(OrderStatus.RETURNED);

		// then
		assertThat(result).isEmpty();
		verify(orderStateRepository).findByState(OrderStatus.RETURNED);
	}
}
