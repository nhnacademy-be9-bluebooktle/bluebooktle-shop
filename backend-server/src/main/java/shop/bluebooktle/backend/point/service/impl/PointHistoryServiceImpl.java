package shop.bluebooktle.backend.point.service.impl;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.point.entity.PointHistory;
import shop.bluebooktle.backend.point.repository.PointHistoryRepository;
import shop.bluebooktle.backend.point.service.PointHistoryService;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.dto.point.request.PointHistoryCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;
import shop.bluebooktle.common.dto.point.response.PointSourceTypeResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointHistoryServiceImpl implements PointHistoryService {

	private final PointHistoryRepository pointHistoryRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public void savePointHistory(Long userId, PointHistoryCreateRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.fromId(request.pointSourceTypeId());

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
	public BigDecimal getTotalPointsByUserId(Long userId) {
		return pointHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged())
			.stream()
			.map(PointHistory::getValue)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
