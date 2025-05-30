package shop.bluebooktle.backend.coupon.batch.direct;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.common.entity.auth.User;

@Component
@StepScope
public class DirectCouponProcessor implements ItemProcessor<User, CouponIssueMessage> {

	@Override
	public CouponIssueMessage process(User user) {
		JobParameters params = Objects.requireNonNull(StepSynchronizationManager.getContext())
			.getStepExecution().getJobParameters();
		Long couponId = params.getLong("couponId");
		LocalDateTime start = params.getLocalDateTime("startAt");
		LocalDateTime end = params.getLocalDateTime("endAt");

		return new CouponIssueMessage(user.getId(), couponId, start, end);
	}
}
