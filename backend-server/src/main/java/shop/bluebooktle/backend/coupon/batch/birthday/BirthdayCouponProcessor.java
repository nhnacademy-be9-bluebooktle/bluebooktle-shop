package shop.bluebooktle.backend.coupon.batch.birthday;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.common.entity.auth.User;

@Component
@RequiredArgsConstructor
@Slf4j
public class BirthdayCouponProcessor implements ItemProcessor<User, CouponIssueMessage> {

	@Override
	public CouponIssueMessage process(User user) {
		JobParameters params = Objects.requireNonNull(StepSynchronizationManager.getContext())
			.getStepExecution().getJobParameters();

		Long couponId = params.getLong("couponId");
		LocalDateTime startAt = params.getLocalDateTime("startAt");
		LocalDateTime endAt = params.getLocalDateTime("endAt");

		return new CouponIssueMessage(user.getId(), couponId, startAt, endAt);
	}
}