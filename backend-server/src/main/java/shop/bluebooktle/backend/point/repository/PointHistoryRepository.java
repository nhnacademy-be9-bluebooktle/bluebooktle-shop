package shop.bluebooktle.backend.point.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.point.entity.PointHistory;
import shop.bluebooktle.common.entity.auth.User;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

	Page<PointHistory> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	Page<PointHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
