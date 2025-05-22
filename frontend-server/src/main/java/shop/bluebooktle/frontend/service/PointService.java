package shop.bluebooktle.frontend.service;

import java.util.List;

import shop.bluebooktle.common.dto.point.request.PointPolicyCreateRequest;
import shop.bluebooktle.common.dto.point.request.PointPolicyUpdateRequest;
import shop.bluebooktle.common.dto.point.request.PointSourceTypeCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointPolicyResponse;
import shop.bluebooktle.common.dto.point.response.PointRuleResponse;
import shop.bluebooktle.common.dto.point.response.PointSourceTypeResponse;

public interface PointService {

	void createPolicy(PointPolicyCreateRequest request);

	PointPolicyResponse getPolicy(Long id);

	void updatePolicy(PointPolicyUpdateRequest request);

	void deletePolicy(Long id);

	List<PointPolicyResponse> getAllPolicies();

	List<PointRuleResponse> getAllRules();

	void createSourceType(PointSourceTypeCreateRequest request);

	PointSourceTypeResponse getSourceType(Long id);

	List<PointSourceTypeResponse> getAllSourceTypes(String actionType);

	void deleteSourceType(Long id);
}
