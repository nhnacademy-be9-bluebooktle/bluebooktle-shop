package shop.bluebooktle.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

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
@EnableScheduling
public class BackendServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendServerApplication.class, args);
	}
}