package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.point.request.PointAdjustmentRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;

public interface PointHistoryService {

	PaginationData<PointHistoryResponse> getMyPointHistories(Pageable pageable);

	void createPointHistory(PointAdjustmentRequest request);

}
