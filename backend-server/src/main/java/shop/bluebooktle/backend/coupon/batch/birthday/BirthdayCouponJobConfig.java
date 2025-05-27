package shop.bluebooktle.backend.coupon.batch.birthday;

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
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.common.entity.auth.User;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BirthdayCouponJobConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final BirthdayUserItemReader reader;
	private final BirthdayCouponProcessor processor;
	private final BirthdayCouponWriter writer;

	@Bean
	public Job birthdayCouponJob() {
		return new JobBuilder("birthdayCouponJob", jobRepository)
			.start(birthdayCouponStep())
			.build();
	}

	@Bean
	public Step birthdayCouponStep() {
		return new StepBuilder("birthdayCouponStep", jobRepository)
			.<User, UserCoupon>chunk(100, transactionManager)
			.reader(reader)
			.processor(processor)
			.writer(writer)
			.build();
	}
}