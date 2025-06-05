package shop.bluebooktle.backend.coupon.batch.birthday;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.common.domain.coupon.PredefinedCoupon;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BirthdayCouponLauncher {

	private final JobLauncher jobLauncher;
	private final Job birthdayCouponJob;
	private final CouponRepository couponRepository;

	public void run() {
		Coupon birthdayCoupon = couponRepository.findById(PredefinedCoupon.BIRTHDAY.getId())
			.orElseThrow(CouponNotFoundException::new);

		LocalDate now = LocalDate.now();
		LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
		LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(LocalTime.MAX);

		JobParameters params = new JobParametersBuilder()
			.addLong("couponId", birthdayCoupon.getId())
			.addLocalDateTime("startAt", startOfMonth)
			.addLocalDateTime("endAt", endOfMonth)
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();

		try {
			jobLauncher.run(birthdayCouponJob, params);
		} catch (Exception e) {
			throw new RuntimeException("생일 쿠폰 batch 실패", e);
		}
	}
}