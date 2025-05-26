package shop.bluebooktle.backend.point.service.impl;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.point.entity.PointHistory;
import shop.bluebooktle.backend.point.entity.PointSourceType;
import shop.bluebooktle.backend.point.repository.PointHistoryRepository;
import shop.bluebooktle.backend.point.repository.PointSourceTypeRepository;
import shop.bluebooktle.backend.point.service.PointHistoryService;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.point.request.PointHistoryCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;
import shop.bluebooktle.common.dto.point.response.PointSourceTypeResponse;
import shop.bluebooktle.common.entity.auth.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointHistoryServiceImpl implements PointHistoryService {

	private final PointHistoryRepository pointHistoryRepository;
	private final PointSourceTypeRepository pointSourceTypeRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public void savePointHistory(Long userId, PointHistoryCreateRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + userId));

		PointSourceType sourceType = pointSourceTypeRepository.findById(request.pointSourceTypeId())
			.orElseThrow(() -> new IllegalArgumentException("포인트 소스 타입을 찾을 수 없습니다: " + request.pointSourceTypeId()));

		PointHistory history = PointHistory.builder()
			.user(user)
			.pointSourceType(sourceType)
			.value(request.value())
			.build();

		pointHistoryRepository.save(history);
	}

	@Override
	public Page<PointHistoryResponse> getPointHistoriesByUserId(Long userId, Pageable pageable) {
		Page<PointHistory> page = pointHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

		return page.map(history -> new PointHistoryResponse(
			history.getId(),
			history.getValue(),
			new PointSourceTypeResponse(
				history.getPointSourceType().getId(),
				history.getPointSourceType().getActionType(),
				history.getPointSourceType().getSourceType()
			),
			history.getCreatedAt()
		));
	}

	@Override
	public BigDecimal getTotalPointsByUserId(Long userId) {
		return pointHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged())
			.stream()
			.map(PointHistory::getValue)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
