package shop.bluebooktle.frontend.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.point.request.PointAdjustmentRequest;
import shop.bluebooktle.common.dto.point.request.PointPolicyCreateRequest;
import shop.bluebooktle.common.dto.point.request.PointPolicyUpdateRequest;
import shop.bluebooktle.common.dto.point.request.PointSourceTypeCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;
import shop.bluebooktle.common.dto.point.response.PointPolicyResponse;
import shop.bluebooktle.common.dto.point.response.PointRuleResponse;
import shop.bluebooktle.common.dto.point.response.PointSourceTypeResponse;
import shop.bluebooktle.frontend.repository.PointHistoryRepository;
import shop.bluebooktle.frontend.repository.PointRepository;
import shop.bluebooktle.frontend.service.PointHistoryService;

@Service
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {
	private final PointHistoryRepository pointHistoryRepository;

	private final PointRepository pointRepository;

	@Override
	public Long createPolicy(PointPolicyCreateRequest request) {
		return pointRepository.createPolicy(request);
	}

	@Override
	public PointPolicyResponse getPolicy(Long id) {
		return pointRepository.getPolicy(id);
	}

	@Override
	public void updatePolicy(PointPolicyUpdateRequest request) {
		pointRepository.updatePolicy(request);
	}

	@Override
	public PaginationData<PointHistoryResponse> getMyPointHistories(Pageable pageable) {
		return pointHistoryRepository.getMyPointHistories(pageable);
	}

	public void deletePolicy(Long id) {
		pointRepository.deletePolicy(id);
	}

	@Override
	public void createPointHistory(PointAdjustmentRequest request) {
		pointHistoryRepository.createPointHistory(request);
	}

	public List<PointPolicyResponse> getAllPolicies() {
		return pointRepository.getAllPolicies();
	}

	// 포인트 로그 생성시 createPointHistory(PointSourceTypeEnum.LOGIN_EARN, 500)
	@Override
	public void createPointHistory(PointSourceTypeEnum sourceType, BigDecimal value) {
		createPointHistory(new PointAdjustmentRequest(sourceType.getId(), value));
	}

	@Override
	public BigDecimal getTotalPoints() {
		return pointHistoryRepository.getTotalPoints();
	}

	public List<PointRuleResponse> getAllRules() {
		return pointRepository.getAllRules();
	}

	@Override
	public Long createSourceType(PointSourceTypeCreateRequest request) {
		return pointRepository.createSourceType(request);
	}

	@Override
	public PointSourceTypeResponse getSourceType(Long id) {
		return pointRepository.getSourceType(id);
	}

	@Override
	public List<PointSourceTypeResponse> getAllSourceTypes(String actionType) {
		return pointRepository.getAllSourceTypes(actionType);
	}

	@Override
	public void deleteSourceType(Long id) {
		pointRepository.deleteSourceType(id);
	}
}
