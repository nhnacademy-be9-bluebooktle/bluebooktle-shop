package shop.bluebooktle.backend.coupon.batch.birthday.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.batch.birthday.BirthdayCouponLauncher;

@Component
@RequiredArgsConstructor
@Slf4j
public class BirthdayCouponScheduler {

	private final BirthdayCouponLauncher launcher;

	@Scheduled(cron = "0 0 0 1 * *")
	public void runBirthdayCouponJob() {
		log.info("생일 쿠폰 발급 스케줄러 실행");
		launcher.run();
	}
}