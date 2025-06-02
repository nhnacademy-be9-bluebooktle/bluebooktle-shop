package shop.bluebooktle.auth.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.auth.repository.UserRepository;
import shop.bluebooktle.auth.repository.point.PointHistoryRepository;
import shop.bluebooktle.auth.repository.point.PointPolicyRepository;
import shop.bluebooktle.auth.repository.point.PointSourceTypeRepository;
import shop.bluebooktle.auth.service.PointService;
import shop.bluebooktle.common.domain.point.ActionType;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.entity.point.PointPolicy;
import shop.bluebooktle.common.entity.point.PointSourceType;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.point.PointPolicyNotFoundException;
import shop.bluebooktle.common.exception.point.PointSourceNotFountException;

@Service

@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
	private final PointHistoryRepository pointHistoryRepository;
	private final PointPolicyRepository pointPolicyRepository;
	private final PointSourceTypeRepository pointSourceTypeRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional()
	public void adjustUserPointAndSavePointHistory(Long userId, PointSourceTypeEnum pointSourceTypeEnum) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		PointSourceType sourceType = pointSourceTypeRepository.findById(pointSourceTypeEnum.getId())
			.orElseThrow(PointSourceNotFountException::new);

		PointPolicy policy = pointPolicyRepository.findByPointSourceType(sourceType).orElseThrow(
			PointPolicyNotFoundException::new);

		BigDecimal pointValue = policy.getValue();

		if (policy.getIsActive() == false) {
			return;
		}
		if (sourceType.getActionType() == ActionType.EARN) {
			user.addPoint(pointValue);
		}

		userRepository.save(user);

		PointHistory history = PointHistory.builder()
			.user(user)
			.sourceType(pointSourceTypeEnum)
			.value(pointValue)
			.build();
		pointHistoryRepository.save(history);
	}

	@Override
	@Transactional()
	public void signUpPoint(Long userId) {
		adjustUserPointAndSavePointHistory(userId, PointSourceTypeEnum.SIGNUP_EARN);
	}

	@Override
	@Transactional()
	public void loginPoint(Long userId) {
		PointHistory last = pointHistoryRepository
			.findTopByUserIdAndSourceTypeOrderByCreatedAtDesc(userId, PointSourceTypeEnum.LOGIN_EARN)
			.orElse(null);

		if (last == null || !last.getCreatedAt().toLocalDate().equals(LocalDate.now())) {
			adjustUserPointAndSavePointHistory(userId, PointSourceTypeEnum.LOGIN_EARN);
		}
	}
	
}
