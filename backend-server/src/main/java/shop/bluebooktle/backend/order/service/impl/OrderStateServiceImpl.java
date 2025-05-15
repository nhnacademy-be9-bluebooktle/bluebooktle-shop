package shop.bluebooktle.backend.order.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.dto.response.OrderStateResponse;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.backend.order.repository.OrderStateRepository;
import shop.bluebooktle.backend.order.service.OrderStateService;
import shop.bluebooktle.common.domain.OrderStatus;

@Service
@RequiredArgsConstructor
public class OrderStateServiceImpl implements OrderStateService {

	private final OrderStateRepository orderStateRepository;

	@Override
	public List<OrderState> getAll() {
		return orderStateRepository.findAll();
	}

	@Override
	public Optional<OrderStateResponse> getByState(OrderStatus state) {
		return orderStateRepository.findByState(state)
			.map(OrderStateResponse::from);
	}
}
