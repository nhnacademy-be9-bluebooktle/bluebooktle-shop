package shop.bluebooktle.backend.user.batch.membershipLevel;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.user.dto.UserMembershipLevelUpdateCommand;
import shop.bluebooktle.backend.user.dto.UserNetSpentAmountDto;

@Configuration
@RequiredArgsConstructor
public class MembershipJobConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final MembershipItemReader reader;
	private final MembershipProcessor processor;
	private final MembershipWriter writer;

	@Bean
	public Job membershipLevelUpdateJob() {
		return new JobBuilder("membershipUpdateJob", jobRepository)
			.start(membershipLevelUpdateStep())
			.build();
	}

	@Bean
	public Step membershipLevelUpdateStep() {
		return new StepBuilder("membershipUpdateStep", jobRepository)
			.<UserNetSpentAmountDto, UserMembershipLevelUpdateCommand>chunk(100, transactionManager)
			.reader(reader)
			.processor(processor)
			.writer(writer)
			.build();
	}
}
