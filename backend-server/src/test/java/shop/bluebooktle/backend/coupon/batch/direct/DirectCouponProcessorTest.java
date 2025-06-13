package shop.bluebooktle.backend.coupon.batch.direct;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.test.MetaDataInstanceFactory;

import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.common.entity.auth.User;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class DirectCouponProcessorTest {

	private final DirectCouponProcessor processor = new DirectCouponProcessor();

	@AfterEach
	void tearDown() {
		StepSynchronizationManager.close(); // context 정리
	}

	@Test
	@DisplayName("쿠폰 메세지 생성")
	void process() {
		// given
		Long couponId = 123L;
		LocalDateTime startAt = LocalDateTime.of(2025, 6, 1, 0, 0);
		LocalDateTime endAt = LocalDateTime.of(2025, 6, 30, 23, 59);

		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("couponId", couponId)
			.addLocalDateTime("startAt", startAt)
			.addLocalDateTime("endAt", endAt)
			.toJobParameters();

		StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
		StepSynchronizationManager.register(new StepContext(stepExecution).getStepExecution());

		User user = User.builder().id(10L).loginId("tester").build();

		// when
		CouponIssueMessage message = processor.process(user);

		// then
		assertThat(message.getUserId()).isEqualTo(10L);
		assertThat(message.getCouponId()).isEqualTo(couponId);
		assertThat(message.getAvailableStartAt()).isEqualTo(startAt);
		assertThat(message.getAvailableEndAt()).isEqualTo(endAt);
	}
}