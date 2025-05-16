package shop.bluebooktle.backend.coupon.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponBatchLauncher {

	private final JobLauncher jobLauncher;
	private final Job couponIssueJob;

	public void run(UserCouponRegisterRequest request) {
		JobParameters params = new JobParametersBuilder()
			.addLong("couponId", request.getCouponId())
			.addLocalDateTime("startAt", request.getAvailableStartAt())
			.addLocalDateTime("endAt", request.getAvailableEndAt())
			.addLong("time", System.currentTimeMillis()) // JobParameters 는 동일한 값으로 실행되면 무시되기 때문에 추가
			.toJobParameters();

		try {
			jobLauncher.run(couponIssueJob, params);
		} catch (Exception e) {
			log.error("쿠폰 발급 배치 실행 실패", e);
			throw new RuntimeException("쿠폰 발급 배치 실행 실패", e);
		}
	}
}
