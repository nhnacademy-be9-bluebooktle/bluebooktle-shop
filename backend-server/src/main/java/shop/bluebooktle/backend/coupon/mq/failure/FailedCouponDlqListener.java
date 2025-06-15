package shop.bluebooktle.backend.coupon.mq.failure;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.backend.coupon.entity.FailedCouponIssue;
import shop.bluebooktle.backend.coupon.mq.properties.QueueProperties;
import shop.bluebooktle.backend.coupon.repository.FailedCouponIssueRepository;
import shop.bluebooktle.common.domain.coupon.CouponIssueStatus;
import shop.bluebooktle.common.domain.coupon.CouponIssueType;

@Slf4j
@Component
@RequiredArgsConstructor
public class FailedCouponDlqListener {
	private final ObjectMapper objectMapper;
	private final FailedCouponIssueRepository failedCouponIssueRepository;
	private final QueueProperties queueProperties;

	@RabbitListener(queues = {
		"${mq.queue.birthday-dlq}",
		"${mq.queue.welcome-dlq}",
		"${mq.queue.direct-dlq}"
	})
	public void listen(Message message) {
		try {
			CouponIssueMessage parsed = objectMapper.readValue(message.getBody(), CouponIssueMessage.class);
			Map<String, Object> headers = message.getMessageProperties().getHeaders();

			String reason = extractReason(headers);
			CouponIssueType issueType = resolveType(message.getMessageProperties().getConsumerQueue());

			FailedCouponIssue entity = FailedCouponIssue.builder()
				.issueId(UUID.randomUUID().toString())
				.issueType(issueType)
				.availableStartAt(parsed.getAvailableStartAt())
				.availableEndAt(parsed.getAvailableEndAt())
				.status(CouponIssueStatus.WAITING)
				.retryCount(0)
				.reason(reason)
				.userId(parsed.getUserId())
				.couponId(parsed.getCouponId())
				.build();

			failedCouponIssueRepository.save(entity);
			log.info("DLQ -> DB 저장 성공 userId={}, couponId={}, type={}, reason={}",
				parsed.getUserId(), parsed.getCouponId(), issueType, reason);

		} catch (Exception e) {
			log.error("DLQ -> DB 저장 실패", e);
		}
	}

	private String extractReason(Map<String, Object> headers) {
		if (headers.containsKey("x-death")) {
			List<Map<String, Object>> deaths = (List<Map<String, Object>>)headers.get("x-death");
			if (!deaths.isEmpty()) {
				return (String)deaths.get(0).get("reason");
			}
		}
		return "unknown";
	}

	private CouponIssueType resolveType(String consumerQueue) {
		if (consumerQueue.equals(queueProperties.getBirthdayDlq()))
			return CouponIssueType.BIRTHDAY;
		if (consumerQueue.equals(queueProperties.getWelcomeDlq()))
			return CouponIssueType.WELCOME;
		if (consumerQueue.equals(queueProperties.getDirectDlq()))
			return CouponIssueType.DIRECT;
		throw new IllegalArgumentException("지원하지 않는 DLQ 큐: " + consumerQueue);
	}
}
