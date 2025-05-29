package shop.bluebooktle.backend.coupon.batch.direct;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.common.entity.auth.User;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DirectCouponIssueJobConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final DirectCouponReader reader;
	private final DirectCouponProcessor processor;
	private final DirectCouponWriter writer;

	@Bean
	public Job directCouponIssueJob() {
		return new JobBuilder("couponIssueJob", jobRepository)
			.start(directCouponIssueStep())
			.build();
	}

	@Bean
	public Step directCouponIssueStep() {
		return new StepBuilder("couponIssueStep", jobRepository)
			.<User, CouponIssueMessage>chunk(100, transactionManager)
			.reader(reader)
			.processor(processor)
			.writer(writer)
			.build();
	}
}
