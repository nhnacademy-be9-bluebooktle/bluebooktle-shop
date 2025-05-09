package shop.bluebooktle.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FrontendServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrontendServerApplication.class, args);
	}
}
