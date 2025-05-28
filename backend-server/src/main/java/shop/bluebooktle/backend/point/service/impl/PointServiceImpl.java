package shop.bluebooktle.backend.point.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.point.service.PointService;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.dto.point.request.PointAdjustmentRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;
import shop.bluebooktle.common.dto.point.response.PointSourceTypeResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.entity.point.PointPolicy;
import shop.bluebooktle.common.entity.point.PointSourceType;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.point.PointPolicyNotFoundException;
import shop.bluebooktle.common.exception.point.PointSourceNotFountException;
import shop.bluebooktle.common.repository.PointHistoryRepository;
import shop.bluebooktle.common.repository.PointPolicyRepository;
import shop.bluebooktle.common.repository.PointSourceTypeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointServiceImpl implements PointService {

	private final PointHistoryRepository pointHistoryRepository;
	private final UserRepository userRepository;
	private final PointSourceTypeRepository pointSourceTypeRepository;
	private final PointPolicyRepository policyRepository;

	@Override
	public void adjustUserPointAndSavePointHistory(Long userId, PointAdjustmentRequest request) {

	}

	@Override
	@Transactional
	public void savePointHistory(Long userId, PointAdjustmentRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.fromId(request.pointSourceTypeId());

		PointSourceType sourceType = pointSourceTypeRepository.findById(request.pointSourceTypeId())
			.orElseThrow(PointSourceNotFountException::new);

		PointPolicy policy = policyRepository.findByPointSourceType(sourceType).orElseThrow(
			PointPolicyNotFoundException::new);

		if (policy.getIsActive() == false) {
			log.info("Point policy is not active for source type: {}", sourceType.getSourceType());
			return;
		}

		PointHistory history = PointHistory.builder()
			.user(user)
			.sourceType(sourceTypeEnum)
			.value(request.value())
			.build();

		pointHistoryRepository.save(history);
	}

	@Override
	public Page<PointHistoryResponse> getPointHistoriesByUserId(Long userId, Pageable pageable) {
		Page<PointHistory> page = pointHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

		return page.map(p -> new PointHistoryResponse(
			p.getId(),
			p.getValue(),
			new PointSourceTypeResponse(
				p.getSourceType().getId(),
				p.getSourceType().getActionType(),
				p.getSourceType().getSourceType()
			),
			p.getCreatedAt()
		));
	}
}
