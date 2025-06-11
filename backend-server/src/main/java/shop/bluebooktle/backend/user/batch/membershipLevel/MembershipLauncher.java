package shop.bluebooktle.backend.user.batch.membershipLevel;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MembershipLauncher {

	private final JobLauncher jobLauncher;
	private final Job membershipLevelUpdateJob;

	public void run() {
		try {
			JobParameters params = new JobParametersBuilder()
				.addLong("runTime", System.currentTimeMillis())
				.toJobParameters();
			log.info("회원 등급 갱신 Job 실행");
			jobLauncher.run(membershipLevelUpdateJob, params);
		} catch (Exception e) {
			throw new RuntimeException("회원 등급 batch 실행에 실패했습니다.", e);
		}
	}
}
