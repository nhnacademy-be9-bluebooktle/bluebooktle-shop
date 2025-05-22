package shop.bluebooktle.backend.coupon.batch.birthday;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BirthdayCouponLauncher {

	private final JobLauncher jobLauncher;

}
