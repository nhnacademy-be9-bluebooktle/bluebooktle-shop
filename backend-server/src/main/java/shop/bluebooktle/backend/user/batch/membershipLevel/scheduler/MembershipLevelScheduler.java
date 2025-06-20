package shop.bluebooktle.backend.user.batch.membershipLevel.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.batch.membershipLevel.MembershipLauncher;

@Component
@RequiredArgsConstructor
@Slf4j
public class MembershipLevelScheduler {

	private final MembershipLauncher launcher;

	@Scheduled(cron = "0 0 * * * *") // 할 일: test 를 위한 1시간 주기 (dev 테스트 후 제거)
	public void runMembershipLevelUpdateJob() {
		log.info("회원 등급 배치 스케줄러 실행");
		launcher.run();
	}
}
