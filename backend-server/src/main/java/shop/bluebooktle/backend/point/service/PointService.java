package shop.bluebooktle.backend.point.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.dto.point.request.PointAdjustmentRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;

public interface PointService {

	void savePointHistory(Long userId, PointAdjustmentRequest request);

	Page<PointHistoryResponse> getPointHistoriesByUserId(Long userId, Pageable pageable);

	void adjustUserPointAndSavePointHistory(Long userId, PointSourceTypeEnum pointSourceTypeEnum, BigDecimal amount);
}
