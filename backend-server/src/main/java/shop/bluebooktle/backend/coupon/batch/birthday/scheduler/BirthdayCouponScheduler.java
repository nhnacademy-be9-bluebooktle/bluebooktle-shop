package shop.bluebooktle.backend.coupon.batch.birthday.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.batch.birthday.BirthdayCouponLauncher;

@Component
@RequiredArgsConstructor
@Slf4j
public class BirthdayCouponScheduler {

	private final BirthdayCouponLauncher launcher;

	// @Scheduled(cron = "0 0 0 1 * *")
	@Scheduled(cron = "0 0/2 * * * *") // TODO dev에서 Test를 위함 -> test 후 변경
	@SchedulerLock(name = "BirthdayCouponScheduler", lockAtLeastFor = "PT1M", lockAtMostFor = "PT5M")
	public void runBirthdayCouponJob() {
		log.info("생일 쿠폰 발급 스케줄러 실행");
		launcher.run();
	}
}