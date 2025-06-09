package shop.bluebooktle.backend.order.mq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mq.order.queue")
public class OrderQueueProperties {
	private String orderWait;
	private String orderCancel;
	private String orderCancelDlq;
}