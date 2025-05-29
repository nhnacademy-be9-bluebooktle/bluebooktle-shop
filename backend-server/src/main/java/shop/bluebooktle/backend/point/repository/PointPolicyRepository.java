package shop.bluebooktle.backend.point.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.common.entity.point.PointPolicy;
import shop.bluebooktle.common.entity.point.PointSourceType;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
	Optional<PointPolicy> findById(Long id);

	void deleteById(Long id);

	Optional<PointPolicy> findByPointSourceType(PointSourceType pointSourceType);

	Optional<PointPolicy> findByPointSourceTypeIdAndIsActiveTrue(Long pointSourceTypeId);

	@EntityGraph(attributePaths = {"pointSourceType"})
	List<PointPolicy> findAll();
}
