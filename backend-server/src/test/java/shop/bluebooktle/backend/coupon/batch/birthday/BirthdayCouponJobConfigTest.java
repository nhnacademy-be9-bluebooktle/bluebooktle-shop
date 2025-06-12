package shop.bluebooktle.backend.coupon.batch.birthday;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;

@ExtendWith(MockitoExtension.class)
class BirthdayCouponJobConfigTest {

	@Mock
	private JobRepository jobRepository;

	@Mock
	private PlatformTransactionManager transactionManager;

	@Mock
	private BirthdayUserItemReader reader;

	@Mock
	private BirthdayCouponProcessor processor;

	@Mock
	private BirthdayCouponWriter writer;

	@InjectMocks
	private BirthdayCouponJobConfig config;

	@Test
	@DisplayName("Job Test")
	void birthdayCouponJob() {
		// when
		Job job = config.birthdayCouponJob();

		// then
		assertThat(job.getName()).isEqualTo("birthdayCouponJob");
	}

	@Test
	@DisplayName("Step Test")
	void birthdayCouponStep() {
		// when
		Step step = config.birthdayCouponStep();

		// then
		assertThat(step.getName()).isEqualTo("birthdayCouponStep");
	}
}