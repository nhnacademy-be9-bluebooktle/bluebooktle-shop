package shop.bluebooktle.backend.coupon.batch.direct;

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
public class DirectCouponBatchLauncher {

	private final JobLauncher jobLauncher;
	private final Job directCouponIssueJob;

	public void run(UserCouponRegisterRequest request) {

		try {
			JobParameters params = new JobParametersBuilder()
				.addLong("couponId", request.getCouponId())
				.addLocalDateTime("startAt", request.getAvailableStartAt())
				.addLocalDateTime("endAt", request.getAvailableEndAt())
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();

			jobLauncher.run(directCouponIssueJob, params);
		} catch (Exception e) {
			log.error("Direct Coupon batch Fail", e);
			throw new RuntimeException("Direct Coupon batch Fail", e);
		}
	}
}
