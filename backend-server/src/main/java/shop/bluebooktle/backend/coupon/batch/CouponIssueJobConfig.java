package shop.bluebooktle.backend.coupon.batch;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.InvalidInputValueException;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CouponIssueJobConfig {
	private final CouponRepository couponRepository;
	private final UserRepository userRepository;
	private final UserCouponRepository userCouponRepository;

	@Bean
	public Job couponIssueJob(JobRepository jobRepository, Step couponIssueStep) {
		return new JobBuilder("couponIssueJob", jobRepository)
			.start(couponIssueStep)
			.build();
	}

	@Bean
	public Step couponIssueStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("couponIssueStep", jobRepository)
			.<User, UserCoupon>chunk(100, transactionManager)
			.reader(userRepositoryReader(userRepository))
			.processor(user -> {
				JobParameters params = Objects.requireNonNull(StepSynchronizationManager.getContext())
					.getStepExecution().getJobParameters();
				Long couponId = params.getLong("couponId");
				LocalDateTime start = params.getLocalDateTime("startAt");
				LocalDateTime end = params.getLocalDateTime("endAt");

				if (couponId == null) {
					throw new InvalidInputValueException();
				}
				Coupon coupon = couponRepository.findById(couponId)
					.orElseThrow(CouponNotFoundException::new);

				return UserCoupon.builder()
					.user(user)
					.coupon(coupon)
					.availableStartAt(start)
					.availableEndAt(end)
					.build();
			})
			.writer(userCoupons -> {
				List<UserCoupon> validCoupons = userCoupons.getItems().stream()
					.filter(Objects::nonNull)
					.collect(Collectors.toList());

				userCouponRepository.saveAll(validCoupons);
			})

			.build();
	}

	@Bean
	@StepScope
	public ItemReader<User> userRepositoryReader(UserRepository userRepository) {

		return new ItemReader<>() {
			private Iterator<User> userIterator;

			@Override
			public User read() {
				if (userIterator == null) {
					List<User> users = userRepository.findByStatus(UserStatus.ACTIVE);
					userIterator = users.iterator();
				}
				if (userIterator.hasNext()) {
					return userIterator.next();
				} else {
					return null;
				}
			}
		};
	}

}
