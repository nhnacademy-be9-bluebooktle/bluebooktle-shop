package shop.bluebooktle.backend.coupon.scheduler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.client.CouponFailureMessageClient;
import shop.bluebooktle.backend.coupon.repository.FailedCouponIssueRepository;
import shop.bluebooktle.backend.user.dto.DoorayMessagePayload;
import shop.bluebooktle.common.domain.coupon.CouponIssueStatus;
import shop.bluebooktle.common.domain.coupon.CouponIssueType;

@Component
@RequiredArgsConstructor
public class CouponFailureSummaryScheduler {

	private final FailedCouponIssueRepository repo;
	private final CouponFailureMessageClient messageClient;

	@Scheduled(cron = "0 0 0 * * *")
	public void notifyTodayFailures() {
		Map<CouponIssueType, Long> totalMap = repo.countTodayTotalByType();
		if (totalMap.isEmpty())
			return;

		Map<CouponIssueType, Long> waitingMap = repo.countTodayByTypeAndStatus(CouponIssueStatus.WAITING);
		Map<CouponIssueType, Long> retriedMap = repo.countTodayByTypeAndStatus(CouponIssueStatus.RETRIED);

		long totalCount = totalMap.values().stream().mapToLong(Long::longValue).sum();
		LocalDate today = LocalDate.now();
		String title = today.format(DateTimeFormatter.ofPattern("M/dd")) + " 실패한 총 쿠폰 개수";

		StringBuilder body = new StringBuilder();
		body.append("총 ").append(totalCount).append("개\n");

		for (CouponIssueType type : CouponIssueType.values()) {
			long total = totalMap.getOrDefault(type, 0L);
			if (total == 0)
				continue;

			long waiting = waitingMap.getOrDefault(type, 0L);
			long retried = retriedMap.getOrDefault(type, 0L);
			body.append(String.format("[%s – 총 %d = 실패 상태 : %d / 재발급 성공 : %d]%n",
				type.name(), total, waiting, retried));
		}

		DoorayMessagePayload payload = new DoorayMessagePayload();
		payload.setBotName(title);
		payload.setText(body.toString().trim());

		messageClient.sendMessage(payload);
	}
}