package shop.bluebooktle.backend.user.batch.dormant;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.entity.auth.User;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DormantUserJobConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final EntityManagerFactory entityManagerFactory;

	private final DormantUserItemReader dormantUserItemReader;
	private final DormantUserProcessor dormantUserProcessor;

	private static final int CHUNK_SIZE = 100;

	@Bean
	public Job dormantUserJob() {
		return new JobBuilder("dormantUserJob", jobRepository)
			.start(dormantUserStep())
			.build();
	}

	@Bean
	public Step dormantUserStep() {
		return new StepBuilder("dormantUserStep", jobRepository)
			.<User, User>chunk(CHUNK_SIZE, transactionManager)
			.reader(dormantUserItemReader)
			.processor(dormantUserProcessor)
			.writer(jpaUserItemWriter())
			.build();
	}

	@Bean
	public JpaItemWriter<User> jpaUserItemWriter() {
		return new JpaItemWriterBuilder<User>()
			.entityManagerFactory(entityManagerFactory)
			.build();
	}
}