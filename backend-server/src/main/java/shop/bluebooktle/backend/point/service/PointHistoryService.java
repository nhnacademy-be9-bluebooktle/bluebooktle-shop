package shop.bluebooktle.backend.point.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.point.request.PointHistoryCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;

public interface PointHistoryService {

	void savePointHistory(Long userId, PointHistoryCreateRequest request);

	Page<PointHistoryResponse> getPointHistoriesByUserId(Long userId, Pageable pageable);

	BigDecimal getTotalPointsByUserId(Long userId);
}
