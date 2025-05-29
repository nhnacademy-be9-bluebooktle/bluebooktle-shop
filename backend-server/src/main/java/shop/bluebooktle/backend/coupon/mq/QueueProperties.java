package shop.bluebooktle.backend.coupon.mq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mq.queue")
public class QueueProperties {
	private String birthday;
	private String birthdayDlq;
	private String direct;
	private String directDlq;
}
