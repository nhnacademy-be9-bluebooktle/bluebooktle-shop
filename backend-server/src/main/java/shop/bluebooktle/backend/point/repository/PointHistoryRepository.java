package shop.bluebooktle.backend.point.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.common.entity.point.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

	Page<PointHistory> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
