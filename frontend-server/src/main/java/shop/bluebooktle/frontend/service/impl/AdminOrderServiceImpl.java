package shop.bluebooktle.frontend.service.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
import shop.bluebooktle.common.dto.order.response.AdminOrderListResponse;
import shop.bluebooktle.frontend.repository.AdminOrderRepository;
import shop.bluebooktle.frontend.service.AdminOrderService;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

	private final AdminOrderRepository adminOrderRepository;

	@Override
	public PaginationData<AdminOrderListResponse> searchOrders(AdminOrderSearchRequest request, Pageable pageable) {
		return adminOrderRepository.searchOrders(request, pageable);
	}
}
