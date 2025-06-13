package shop.bluebooktle.backend.coupon.batch.direct;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

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

import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;

@ExtendWith(MockitoExtension.class)
class DirectCouponBatchLauncherTest {

	@Mock
	private JobLauncher jobLauncher;

	@Mock
	private Job directCouponIssueJob;

	@InjectMocks
	private DirectCouponBatchLauncher launcher;

	@Test
	@DisplayName("배치 성공")
	void run_success() throws Exception {
		// given
		UserCouponRegisterRequest request = UserCouponRegisterRequest.builder()
			.couponId(123L)
			.availableStartAt(LocalDateTime.now())
			.availableEndAt(LocalDateTime.now().plusDays(3))
			.build();

		when(jobLauncher.run(eq(directCouponIssueJob), any(JobParameters.class)))
			.thenReturn(mock(JobExecution.class));

		// when
		launcher.run(request);

		// then
		verify(jobLauncher).run(eq(directCouponIssueJob), any(JobParameters.class));
	}

	@Test
	@DisplayName("배치 실패")
	void run_fail() throws Exception {
		// given
		UserCouponRegisterRequest request = UserCouponRegisterRequest.builder()
			.couponId(123L)
			.availableStartAt(LocalDateTime.now())
			.availableEndAt(LocalDateTime.now().plusDays(3))
			.build();

		when(jobLauncher.run(eq(directCouponIssueJob), any(JobParameters.class)))
			.thenThrow(new RuntimeException("실패"));

		// when & then
		assertThatThrownBy(() -> launcher.run(request))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Direct Coupon batch Fail");
	}
}