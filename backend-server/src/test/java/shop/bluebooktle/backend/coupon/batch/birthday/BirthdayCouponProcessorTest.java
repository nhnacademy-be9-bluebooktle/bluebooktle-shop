package shop.bluebooktle.backend.coupon.batch.birthday;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.test.MetaDataInstanceFactory;

import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.common.entity.auth.User;

class BirthdayCouponProcessorTest {

	private final BirthdayCouponProcessor processor = new BirthdayCouponProcessor();

	@AfterEach
	void clearStepContext() {
		StepSynchronizationManager.close();
	}

	@Test
	@DisplayName("processer 메세지 정상 생성")
	void process() {
		// given
		Long couponId = 100L;
		LocalDateTime startAt = LocalDateTime.now();
		LocalDateTime endAt = startAt.plusDays(3);

		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("couponId", couponId)
			.addLocalDateTime("startAt", startAt)
			.addLocalDateTime("endAt", endAt)
			.toJobParameters();

		StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
		StepSynchronizationManager.register(new StepContext(stepExecution).getStepExecution());

		User user = User.builder().id(1L).loginId("tester").build();

		// when
		CouponIssueMessage message = processor.process(user);

		// then
		assertThat(message).isNotNull();
		assertThat(message.getUserId()).isEqualTo(1L);
		assertThat(message.getCouponId()).isEqualTo(couponId);
		assertThat(message.getAvailableStartAt()).isEqualTo(startAt);
		assertThat(message.getAvailableEndAt()).isEqualTo(endAt);
	}
}