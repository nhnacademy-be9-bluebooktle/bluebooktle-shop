package shop.bluebooktle.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
	"shop.bluebooktle.auth",
	"shop.bluebooktle.common"
})
@EnableDiscoveryClient
@EntityScan(basePackages = {"shop.bluebooktle.common.entity"})
@EnableJpaRepositories(basePackages = {"shop.bluebooktle.common.repository",
	"shop.bluebooktle.auth"})
@EnableJpaAuditing
@EnableFeignClients
public class AuthServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}
}