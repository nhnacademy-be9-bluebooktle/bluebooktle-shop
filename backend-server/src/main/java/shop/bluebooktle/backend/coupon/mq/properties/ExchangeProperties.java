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
@ConfigurationProperties(prefix = "mq.exchange")
public class ExchangeProperties {
	private String birthday;
	private String birthdayDlx;
	private String direct;
	private String directDlx;
	private String welcome;
	private String welcomeDlx;
}