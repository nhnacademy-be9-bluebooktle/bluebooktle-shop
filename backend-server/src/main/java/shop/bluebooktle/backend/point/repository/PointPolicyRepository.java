package shop.bluebooktle.backend.point.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.point.entity.PointPolicy;
import shop.bluebooktle.backend.point.entity.PointSourceType;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
	Optional<PointPolicy> findById(Long id);

	void deleteById(Long id);

	Optional<PointPolicy> findByPointSourceType(PointSourceType pointSourceType);

	@EntityGraph(attributePaths = {"pointSourceType"})
	List<PointPolicy> findAll();
}
