package shop.bluebooktle.backend.point.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.point.request.PointAdjustmentRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;

public interface PointService {

	void savePointHistory(Long userId, PointAdjustmentRequest request);

	Page<PointHistoryResponse> getPointHistoriesByUserId(Long userId, Pageable pageable);

}
