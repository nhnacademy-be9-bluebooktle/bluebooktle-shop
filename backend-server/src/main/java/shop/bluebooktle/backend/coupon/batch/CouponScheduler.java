package shop.bluebooktle.backend.coupon.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponScheduler {

	private final JobLauncher jobLauncher;
	private final Job birthdayCouponJob;

	@Scheduled(cron = "0 0 0 1 * *")
	public void runBirthdayCouponJob() {
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();

			jobLauncher.run(birthdayCouponJob, jobParameters);
			log.info("생일 쿠폰 발급에 성공했습니다.");
		} catch (Exception e) {
			log.error("생일 쿠폰 발급에 실패했습니다.", e);
		}
	}

}
