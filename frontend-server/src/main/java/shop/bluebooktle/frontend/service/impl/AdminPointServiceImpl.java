package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.point.request.PointPolicyCreateRequest;
import shop.bluebooktle.common.dto.point.request.PointPolicyUpdateRequest;
import shop.bluebooktle.common.dto.point.request.PointSourceTypeCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointPolicyResponse;
import shop.bluebooktle.common.dto.point.response.PointRuleResponse;
import shop.bluebooktle.common.dto.point.response.PointSourceTypeResponse;
import shop.bluebooktle.frontend.repository.AdminPointRepository;
import shop.bluebooktle.frontend.service.AdminPointService;

@Service
@RequiredArgsConstructor
public class AdminPointServiceImpl implements AdminPointService {

	private final AdminPointRepository pointRepository;

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
	public void deletePolicy(Long id) {
		pointRepository.deletePolicy(id);
	}

	@Override
	public List<PointPolicyResponse> getAllPolicies() {
		return pointRepository.getAllPolicies();
	}

	@Override
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
