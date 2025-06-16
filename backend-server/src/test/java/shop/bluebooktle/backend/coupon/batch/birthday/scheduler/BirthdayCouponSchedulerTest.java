package shop.bluebooktle.backend.coupon.batch.birthday.scheduler;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.backend.coupon.batch.birthday.BirthdayCouponLauncher;
import shop.bluebooktle.backend.coupon.scheduler.BirthdayCouponScheduler;

class BirthdayCouponSchedulerTest {

	private BirthdayCouponLauncher launcher;
	private BirthdayCouponScheduler scheduler;

	@BeforeEach
	void setUp() {
		launcher = mock(BirthdayCouponLauncher.class);
		scheduler = new BirthdayCouponScheduler(launcher);
	}

	@Test
	void runBirthdayCouponJob_shouldCallLauncherRun() {
		// when
		scheduler.runBirthdayCouponJob();

		// then
		verify(launcher).run();
	}
}
