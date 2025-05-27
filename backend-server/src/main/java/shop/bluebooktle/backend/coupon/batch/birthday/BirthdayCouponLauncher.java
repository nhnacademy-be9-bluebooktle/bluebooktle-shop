package shop.bluebooktle.backend.coupon.batch.birthday;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BirthdayCouponLauncher {

	private final JobLauncher jobLauncher;
	private final Job birthdayCouponJob;

	public void run() {
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();

			jobLauncher.run(birthdayCouponJob, jobParameters);
			log.info("Launcher : 생일 쿠폰 발급에 성공했습니다.");
		} catch (Exception e) {
			log.error("Launcher : 생일 쿠폰 발급에 실패했습니다.", e);
			throw new RuntimeException("생일 쿠폰 배치 실패", e);
		}
	}
}