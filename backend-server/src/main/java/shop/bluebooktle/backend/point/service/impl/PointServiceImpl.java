package shop.bluebooktle.backend.point.service.impl;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.point.repository.PointHistoryRepository;
import shop.bluebooktle.backend.point.repository.PointPolicyRepository;
import shop.bluebooktle.backend.point.repository.PointSourceTypeRepository;
import shop.bluebooktle.backend.point.service.PointService;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.point.ActionType;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointServiceImpl implements PointService {

	private final PointHistoryRepository pointHistoryRepository;
	private final UserRepository userRepository;
	private final PointSourceTypeRepository pointSourceTypeRepository;
	private final PointPolicyRepository pointPolicyRepository;

	@Override
	@Transactional
	public void savePointHistory(Long userId, PointAdjustmentRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.fromId(request.pointSourceTypeId());

		PointSourceType sourceType = pointSourceTypeRepository.findById(request.pointSourceTypeId())
			.orElseThrow(PointSourceNotFountException::new);

		PointPolicy policy = pointPolicyRepository.findByPointSourceType(sourceType).orElseThrow(
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

	@Override
	@Transactional
	public void adjustUserPointAndSavePointHistory(Long userId, PointSourceTypeEnum pointSourceTypeEnum,
		BigDecimal amount) {
		User user = userRepository.findUserById(userId)
			.orElseThrow(UserNotFoundException::new);

		PointSourceType sourceType = pointSourceTypeRepository.findById(pointSourceTypeEnum.getId())
			.orElseThrow(PointSourceNotFountException::new);

		PointPolicy policy = pointPolicyRepository.findByPointSourceType(sourceType).orElseThrow(
			PointPolicyNotFoundException::new);

		if (policy.getIsActive() == false) {
			return;
		}
		BigDecimal pointValue;
		if (sourceType.getActionType() == ActionType.EARN) {
			if (sourceType.getId().equals(PointSourceTypeEnum.PAYMENT_EARN.getId())) {
				pointValue = amount.multiply((policy.getValue().add(
						BigDecimal.valueOf(user.getMembershipLevel().getRate()))))
					.divideToIntegralValue(BigDecimal.valueOf(100L));
			} else {
				pointValue = policy.getValue();
			}
			user.addPoint(pointValue);
		} else {
			pointValue = amount;
			user.subtractPoint(pointValue);
		}

		userRepository.save(user);

		PointHistory history = PointHistory.builder()
			.user(user)
			.sourceType(pointSourceTypeEnum)
			.value(pointValue)
			.build();
		pointHistoryRepository.save(history);
	}
}
