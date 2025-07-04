package shop.bluebooktle.backend.point.service.impl;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.repository.PaymentRepository;
import shop.bluebooktle.backend.point.entity.PaymentPointHistory;
import shop.bluebooktle.backend.point.repository.PaymentPointHistoryRepository;
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
import shop.bluebooktle.common.exception.payment.PaymentNotFoundException;
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
	private final PaymentRepository paymentRepository;
	private final PaymentPointHistoryRepository paymentPointHistoryRepository;

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
		Page<PointHistory> page = pointHistoryRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);

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
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void reviewPoint(Long userId) {
		this.adjustUserPointAndSavePointHistory(userId, PointSourceTypeEnum.REVIEW_EARN, null, null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void adjustUserPointAndSavePointHistory(Long userId, PointSourceTypeEnum pointSourceTypeEnum,
		BigDecimal amount, Long paymentId) {
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
				if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
					return;
				}
				pointValue = amount.multiply((policy.getValue().add(
						BigDecimal.valueOf(user.getMembershipLevel().getRate()))))
					.divideToIntegralValue(BigDecimal.valueOf(100L));
			} else {
				pointValue = policy.getValue();
			}
			user.addPoint(pointValue);
		} else {
			if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
				return;
			}
			pointValue = amount;
			user.subtractPoint(pointValue);
		}

		userRepository.save(user);

		if (pointValue == null || pointValue.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		PointHistory history = PointHistory.builder()
			.user(user)
			.sourceType(pointSourceTypeEnum)
			.value(pointValue)
			.build();
		PointHistory savedPointHistory = pointHistoryRepository.save(history);

		if (paymentId != null) {
			Payment payment = paymentRepository.findById(paymentId).orElseThrow(PaymentNotFoundException::new);
			paymentPointHistoryRepository.save(new PaymentPointHistory(payment, savedPointHistory));
		}
	}
}
