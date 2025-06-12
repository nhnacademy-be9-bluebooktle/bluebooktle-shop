package shop.bluebooktle.backend.coupon.batch.birthday;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.common.domain.coupon.PredefinedCoupon;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;

@ExtendWith(MockitoExtension.class)
class BirthdayCouponLauncherTest {

	@Mock
	private JobLauncher jobLauncher;

	@Mock
	private Job birthdayCouponJob;

	@Mock
	private CouponRepository couponRepository;

	@InjectMocks
	private BirthdayCouponLauncher launcher;

	@Test
	@DisplayName("배치 작업 성공")
	void run() throws Exception {
		// given
		Long couponId = PredefinedCoupon.BIRTHDAY.getId();
		Coupon coupon = Coupon.builder().id(couponId).couponName("생일쿠폰").build();

		when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
		when(jobLauncher.run(eq(birthdayCouponJob), any(JobParameters.class)))
			.thenReturn(mock(JobExecution.class));

		// when
		launcher.run();

		// then
		verify(couponRepository).findById(couponId);
		verify(jobLauncher).run(eq(birthdayCouponJob), any(JobParameters.class));
	}

	@Test
	@DisplayName("쿠폰이 존재하지 않으면 CouponNotFoundException 발생")
	void run_fail_CouponNotFound() {
		// given
		Long couponId = PredefinedCoupon.BIRTHDAY.getId();
		when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> launcher.run())
			.isInstanceOf(CouponNotFoundException.class);
	}

	@Test
	@DisplayName("JobLauncher 예외 발생 시 RuntimeException")
	void run_fail_RuntimeException() throws Exception {
		// given
		Long couponId = PredefinedCoupon.BIRTHDAY.getId();
		Coupon coupon = Coupon.builder().id(couponId).couponName("생일쿠폰").build();

		when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
		when(jobLauncher.run(eq(birthdayCouponJob), any(JobParameters.class)))
			.thenThrow(new RuntimeException("job 실행 실패"));

		// when & then
		assertThatThrownBy(() -> launcher.run())
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("생일 쿠폰 batch 실패");
	}
}