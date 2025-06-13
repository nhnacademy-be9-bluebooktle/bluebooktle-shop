package shop.bluebooktle.backend.coupon.batch.direct;

import static org.assertj.core.api.Assertions.*;

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
class DirectCouponIssueJobConfigTest {

	@Mock
	private JobRepository jobRepository;

	@Mock
	private PlatformTransactionManager transactionManager;

	@Mock
	private DirectCouponReader reader;

	@Mock
	private DirectCouponProcessor processor;

	@Mock
	private DirectCouponWriter writer;

	@InjectMocks
	private DirectCouponIssueJobConfig config;

	@Test
	@DisplayName("Job Test")
	void directCouponIssueJob() {
		// when
		Job job = config.directCouponIssueJob();

		// then
		assertThat(job.getName()).isEqualTo("couponIssueJob");
	}

	@Test
	@DisplayName("Step Test")
	void directCouponIssueStep_shouldReturnCorrectStep() {
		// when
		Step step = config.directCouponIssueStep();

		// then
		assertThat(step.getName()).isEqualTo("couponIssueStep");
	}
}