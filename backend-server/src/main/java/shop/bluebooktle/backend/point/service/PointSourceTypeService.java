package shop.bluebooktle.backend.point.service;

import java.util.List;

import shop.bluebooktle.common.domain.point.ActionType;
import shop.bluebooktle.common.dto.point.request.PointSourceTypeCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointSourceTypeResponse;

public interface PointSourceTypeService {

	void create(PointSourceTypeCreateRequest request);

	void delete(Long id);

	PointSourceTypeResponse get(Long id);

	List<PointSourceTypeResponse> getAll();

	List<PointSourceTypeResponse> getAllByActionType(ActionType actionType);

}
