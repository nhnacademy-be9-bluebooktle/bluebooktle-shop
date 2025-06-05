package shop.bluebooktle.frontend.service;

import java.util.List;
import java.math.BigDecimal;

import shop.bluebooktle.common.dto.point.request.PointPolicyCreateRequest;
import shop.bluebooktle.common.dto.point.request.PointPolicyUpdateRequest;
import shop.bluebooktle.common.dto.point.request.PointSourceTypeCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointPolicyResponse;
import shop.bluebooktle.common.dto.point.response.PointRuleResponse;
import shop.bluebooktle.common.dto.point.response.PointSourceTypeResponse;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.point.request.PointAdjustmentRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;

public interface PointHistoryService {

	PaginationData<PointHistoryResponse> getMyPointHistories(Pageable pageable);
	Long createPolicy(PointPolicyCreateRequest request);

	PointPolicyResponse getPolicy(Long id);

	void updatePolicy(PointPolicyUpdateRequest request);

	void deletePolicy(Long id);

	List<PointPolicyResponse> getAllPolicies();

	void createPointHistory(PointAdjustmentRequest request);

	void createPointHistory(PointSourceTypeEnum sourceType, BigDecimal value);


	BigDecimal getTotalPoints();
	List<PointRuleResponse> getAllRules();

	Long createSourceType(PointSourceTypeCreateRequest request);

	PointSourceTypeResponse getSourceType(Long id);

	List<PointSourceTypeResponse> getAllSourceTypes(String actionType);

	void deleteSourceType(Long id);
}
