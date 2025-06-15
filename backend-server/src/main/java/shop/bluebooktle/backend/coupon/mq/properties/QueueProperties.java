package shop.bluebooktle.backend.coupon.mq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@RefreshScope
@ConfigurationProperties(prefix = "mq.queue")
public class QueueProperties {
	private String birthday;
	private String birthdayDlq;
	private String direct;
	private String directDlq;
	private String welcome;
	private String welcomeDlq;
}
