package shop.bluebooktle.backend.point.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.point.entity.PointSourceType;
import shop.bluebooktle.common.domain.point.ActionType;

public interface PointSourceTypeRepository extends JpaRepository<PointSourceType, Long> {
	Optional<PointSourceType> findById(Long id);

	List<PointSourceType> findAllByActionType(ActionType actionType);
}
