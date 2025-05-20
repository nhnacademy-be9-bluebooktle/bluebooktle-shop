package shop.bluebooktle.backend.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.point.entity.PointSourceType;

public interface PointSourceTypeRepository extends JpaRepository<PointSourceType, Integer> {
}
