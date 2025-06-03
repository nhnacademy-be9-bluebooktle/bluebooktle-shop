package shop.bluebooktle.auth.mq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mq.exchange")
public class ExchangeProperties {
	private String welcome;
}
