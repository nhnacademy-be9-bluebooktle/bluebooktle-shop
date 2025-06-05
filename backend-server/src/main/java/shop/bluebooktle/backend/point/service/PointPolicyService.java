package shop.bluebooktle.backend.point.service;

import java.util.List;

import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.dto.point.request.PointPolicyCreateRequest;
import shop.bluebooktle.common.dto.point.request.PointPolicyUpdateRequest;
import shop.bluebooktle.common.dto.point.response.PointPolicyResponse;
import shop.bluebooktle.common.dto.point.response.PointRuleResponse;
import shop.bluebooktle.common.entity.point.PointSourceType;

public interface PointPolicyService {

	Long create(PointPolicyCreateRequest request);

	void update(PointPolicyUpdateRequest request);

	void delete(Long id);

	PointPolicyResponse get(Long id);

	PointPolicyResponse getByPointSourceType(PointSourceType sourceType);

	List<PointRuleResponse> getAll();

	List<PointPolicyResponse> findAll();

	PointRuleResponse getRuleBySourceTypeEnum(PointSourceTypeEnum sourceTypeEnum);
}
