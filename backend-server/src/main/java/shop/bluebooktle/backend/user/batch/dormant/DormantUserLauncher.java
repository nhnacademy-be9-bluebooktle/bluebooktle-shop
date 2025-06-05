package shop.bluebooktle.backend.user.batch.dormant;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DormantUserLauncher {

	private final JobLauncher jobLauncher;
	private final Job dormantUserJob;

	public void run() {
		try {
			JobParameters params = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();
			jobLauncher.run(dormantUserJob, params);

		} catch (Exception e) {
			throw new RuntimeException("사용자 휴면 batch 실행에 실패했습니다.", e);
		}
	}
}