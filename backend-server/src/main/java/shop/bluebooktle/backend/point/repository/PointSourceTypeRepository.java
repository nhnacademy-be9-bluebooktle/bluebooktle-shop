package shop.bluebooktle.backend.point.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.common.domain.point.ActionType;
import shop.bluebooktle.common.entity.point.PointSourceType;

public interface PointSourceTypeRepository extends JpaRepository<PointSourceType, Long> {

	List<PointSourceType> findAllByActionType(ActionType actionType);

}
