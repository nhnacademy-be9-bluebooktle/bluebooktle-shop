package shop.bluebooktle.backend.order.mq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mq.order.queue")
@RefreshScope
public class OrderQueueProperties {
	private String orderWait;
	private String orderCancel;
	private String orderCancelDlq;
	private String orderShipping;
	private String orderComplete;
	private String orderCompleteDlq;
}