package shop.bluebooktle.frontend.service.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.point.request.PointAdjustmentRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;
import shop.bluebooktle.frontend.repository.PointHistoryRepository;
import shop.bluebooktle.frontend.service.PointHistoryService;

@Service
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {
	private final PointHistoryRepository pointHistoryRepository;

	@Override
	public PaginationData<PointHistoryResponse> getMyPointHistories(Pageable pageable) {
		return pointHistoryRepository.getMyPointHistories(pageable);
	}

	@Override
	public void createPointHistory(PointAdjustmentRequest request) {
		pointHistoryRepository.createPointHistory(request);
	}

}
