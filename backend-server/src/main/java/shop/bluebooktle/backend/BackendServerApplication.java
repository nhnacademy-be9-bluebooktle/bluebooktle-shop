package shop.bluebooktle.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
	"shop.bluebooktle.backend",
	"shop.bluebooktle.common"
})
@EnableFeignClients
@EntityScan(basePackages = {
	"shop.bluebooktle.backend",
	"shop.bluebooktle.common.entity"
})
@EnableAspectJAutoProxy()
@EnableJpaRepositories(basePackages = {"shop.bluebooktle.common",
	"shop.bluebooktle.backend"})
public class BackendServerApplication {
	public static void main(String[] args) {
		// build 테스트
		SpringApplication.run(BackendServerApplication.class, args);
	}
}