package shop.bluebooktle.backend.coupon.mq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mq.exchange")
public class ExchangeProperties {
	private String birthday;
	private String birthdayDlx;
	private String direct;
	private String directDlx;
}