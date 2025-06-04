package shop.bluebooktle.backend.user.batch.dormant.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.batch.dormant.DormantUserLauncher;

@Component
@RequiredArgsConstructor
@Slf4j
public class DormantUserScheduler {

	private final DormantUserLauncher dormantUserLauncher;

	@Scheduled(cron = "0 0 3 * * *")
	public void runDormantUserJobDaily() {
		log.info("휴면 사용자 처리 스케줄러 시작.(매일 03:00)");
		dormantUserLauncher.run();
	}
}