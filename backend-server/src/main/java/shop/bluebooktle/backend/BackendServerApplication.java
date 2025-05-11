package shop.bluebooktle.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableFeignClients
@EntityScan(basePackages = {
	"shop.bluebooktle.backend",
	"shop.bluebooktle.common.entity"
})
@EnableJpaAuditing
public class BackendServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendServerApplication.class, args);
	}
}
