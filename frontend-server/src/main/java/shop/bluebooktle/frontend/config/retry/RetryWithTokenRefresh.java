package shop.bluebooktle.frontend.config.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import shop.bluebooktle.frontend.exception.TokenRefreshAndRetryNeededException;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Retryable(
	retryFor = {TokenRefreshAndRetryNeededException.class},
	maxAttempts = 2,
	backoff = @Backoff(delay = 150L)
)
public @interface RetryWithTokenRefresh {

}