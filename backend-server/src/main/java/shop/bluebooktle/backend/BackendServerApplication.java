package shop.bluebooktle.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {
	"shop.bluebooktle.backend",
	"shop.bluebooktle.common"
})
@EnableFeignClients
@EnableJpaAuditing //알라딘 도서등록구현하는데 생성일 관련오류나서 추가. 문제생긴다면 삭제해주세요
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
