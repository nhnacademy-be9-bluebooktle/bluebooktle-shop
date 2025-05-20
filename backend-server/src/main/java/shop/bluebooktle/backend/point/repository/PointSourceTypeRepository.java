package shop.bluebooktle.backend.point.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.point.entity.PointSourceType;

public interface PointSourceTypeRepository extends JpaRepository<PointSourceType, Integer> {
	Optional<PointSourceType> findById(Long id);
	
}
