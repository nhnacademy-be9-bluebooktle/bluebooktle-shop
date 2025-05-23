package shop.bluebooktle.backend.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.point.entity.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
	
}
