package shop.bluebooktle.backend.coupon.batch.birthday.scheduler;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
	private final RedissonClient redissonClient;

	// @Scheduled(cron = "0 0 0 1 * *")
	@Scheduled(cron = "0 0/5 * * * *") // TODO dev에서 Test를 위함 -> test 후 변경
	public void runBirthdayCouponJob() {
		RLock lock = redissonClient.getLock("lock:BirthdayCouponScheduler");
		boolean acquired = false;

		try {
			acquired = lock.tryLock(3, 30, TimeUnit.SECONDS); //최대 3초 대기, 30초 락
			if (acquired) {
				log.info("생일 쿠폰 발급 스케줄러 실행");
				launcher.run();
			} else {
				log.info("다른 인스턴스가 처리 중이라 락 걸림");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			if (acquired && lock.isHeldByCurrentThread()) {
				lock.unlock();
				log.info("락 해제");
			}
		}
	}
}