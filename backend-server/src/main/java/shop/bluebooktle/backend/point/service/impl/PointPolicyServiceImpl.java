package shop.bluebooktle.backend.point.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.point.repository.PointPolicyRepository;
import shop.bluebooktle.backend.point.repository.PointSourceTypeRepository;
import shop.bluebooktle.backend.point.service.PointPolicyService;
import shop.bluebooktle.common.domain.point.ActionType;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.dto.point.request.PointPolicyCreateRequest;
import shop.bluebooktle.common.dto.point.request.PointPolicyUpdateRequest;
import shop.bluebooktle.common.dto.point.response.PointPolicyResponse;
import shop.bluebooktle.common.dto.point.response.PointRuleResponse;
import shop.bluebooktle.common.entity.point.PointPolicy;
import shop.bluebooktle.common.entity.point.PointSourceType;
import shop.bluebooktle.common.exception.point.PointPolicyCreationNotAllowedException;
import shop.bluebooktle.common.exception.point.PointPolicyNotFoundException;
import shop.bluebooktle.common.exception.point.PointSourceNotFountException;

@Service
@RequiredArgsConstructor
@Transactional
public class PointPolicyServiceImpl implements PointPolicyService {

	private final PointPolicyRepository pointPolicyRepository;
	private final PointSourceTypeRepository pointSourceTypeRepository;

	@Override
	public Long create(PointPolicyCreateRequest request) {
		PointSourceType pst = pointSourceTypeRepository.findById(request.pointSourceTypeId())
			.orElseThrow(PointSourceNotFountException::new);

		if (pst.getActionType() == ActionType.USE) {
			throw new PointPolicyCreationNotAllowedException();
		}

		PointPolicy pointPolicy = pointPolicyRepository.save(
			PointPolicy.builder()
				.pointSourceType(pst)
				.policyType(request.policyType())
				.value(request.value())
				.build()
		);
		return pointPolicy.getId();
	}

	@Override
	public void update(PointPolicyUpdateRequest request) {
		PointPolicy policy = pointPolicyRepository.findById(request.pointPolicyId())
			.orElseThrow(PointSourceNotFountException::new);

		if (request.policyType() != null) {
			policy.changePolicyType(request.policyType());
		}
		if (request.value() != null) {
			policy.changeValue(request.value());
		}
		if (request.isActive() != null) {
			policy.changeIsActive(request.isActive());
		}
		pointPolicyRepository.save(policy);
	}

	@Override
	public void delete(Long id) {
		PointPolicy policy = pointPolicyRepository.findById(id)
			.orElseThrow(PointSourceNotFountException::new);
		pointPolicyRepository.delete(policy);
	}

	@Override
	@Transactional(readOnly = true)
	public PointPolicyResponse get(Long id) {
		PointPolicy policy = pointPolicyRepository.findById(id)
			.orElseThrow(PointSourceNotFountException::new);

		return new PointPolicyResponse(
			policy.getId(),
			policy.getPolicyType(),
			policy.getValue(),
			policy.getIsActive()
		);
	}

	@Override
	public PointPolicyResponse getByPointSourceType(PointSourceType sourceType) {
		PointPolicy policy = pointPolicyRepository.findByPointSourceType(sourceType)
			.orElseThrow(PointSourceNotFountException::new);

		return new PointPolicyResponse(
			policy.getId(),
			policy.getPolicyType(),
			policy.getValue(),
			policy.getIsActive()
		);
	}

	@Override
	public List<PointRuleResponse> getAll() {
		List<PointPolicy> pointPolicies = pointPolicyRepository.findAll();
		List<PointRuleResponse> pointRuleResponses = new ArrayList<>();
		for (PointPolicy policy : pointPolicies) {
			PointSourceType pointSourceType = policy.getPointSourceType();
			pointRuleResponses.add(new PointRuleResponse(
					policy.getId(),
					pointSourceType.getId(),
					pointSourceType.getSourceType(),
					policy.getPolicyType(),
					policy.getValue(),
					policy.getIsActive()
				)
			);
		}
		return pointRuleResponses;
	}

	@Override
	public List<PointPolicyResponse> findAll() {
		return pointPolicyRepository.findAll().stream().map(p -> new PointPolicyResponse(
			p.getId(),
			p.getPolicyType(),
			p.getValue(),
			p.getIsActive()
		)).toList();
	}

	@Override
	public PointRuleResponse getRuleBySourceTypeEnum(PointSourceTypeEnum sourceTypeEnum) {
		PointSourceType sourceType = pointSourceTypeRepository.findById(sourceTypeEnum.getId())
			.orElseThrow(PointSourceNotFountException::new);

		PointPolicy policy = pointPolicyRepository.findByPointSourceType(sourceType).orElseThrow(
			PointPolicyNotFoundException::new);

		return new PointRuleResponse(
			policy.getId(),
			sourceType.getId(),
			sourceType.getSourceType(),
			policy.getPolicyType(),
			policy.getValue(),
			policy.getIsActive()
		);
	}
}
