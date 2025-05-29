package shop.bluebooktle.backend.point.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

	Page<PointHistory> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	Page<PointHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

	Optional<PointHistory> findTopByUserIdAndSourceTypeOrderByCreatedAtDesc(Long userId,
		PointSourceTypeEnum sourceType);
}
