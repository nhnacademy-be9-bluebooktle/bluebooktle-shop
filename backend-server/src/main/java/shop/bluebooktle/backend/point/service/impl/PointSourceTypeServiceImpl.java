package shop.bluebooktle.backend.point.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.point.service.PointSourceTypeService;
import shop.bluebooktle.common.domain.point.ActionType;
import shop.bluebooktle.common.dto.point.request.PointSourceTypeCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointSourceTypeResponse;
import shop.bluebooktle.common.entity.point.PointSourceType;
import shop.bluebooktle.common.exception.point.PointSourceNotFountException;
import shop.bluebooktle.common.repository.PointSourceTypeRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class PointSourceTypeServiceImpl implements PointSourceTypeService {

	private final PointSourceTypeRepository pointSourceTypeRepository;

	@Override
	public Long create(PointSourceTypeCreateRequest request) {
		PointSourceType pointSourceType = PointSourceType.builder()
			.actionType(request.actionType())
			.sourceType(request.sourceType())
			.build();
		PointSourceType saved = pointSourceTypeRepository.save(pointSourceType);
		return saved.getId();
	}

	@Override
	public void delete(Long id) {
		if (!pointSourceTypeRepository.existsById(id)) {
			throw new PointSourceNotFountException();
		}
		pointSourceTypeRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public PointSourceTypeResponse get(Long id) {
		PointSourceType pointSourceType = pointSourceTypeRepository.findById(id)
			.orElseThrow(PointSourceNotFountException::new);

		return new PointSourceTypeResponse(
			pointSourceType.getId(),
			pointSourceType.getActionType(),
			pointSourceType.getSourceType()
		);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PointSourceTypeResponse> getAll() {
		return pointSourceTypeRepository.findAll()
			.stream()
			.map(pointSourceType -> new PointSourceTypeResponse(
				pointSourceType.getId(),
				pointSourceType.getActionType(),
				pointSourceType.getSourceType()
			))
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<PointSourceTypeResponse> getAllByActionType(ActionType actionType) {
		return pointSourceTypeRepository.findAllByActionType(actionType)
			.stream()
			.map(pointSourceType -> new PointSourceTypeResponse(
				pointSourceType.getId(),
				pointSourceType.getActionType(),
				pointSourceType.getSourceType()
			))
			.toList();
	}
}
