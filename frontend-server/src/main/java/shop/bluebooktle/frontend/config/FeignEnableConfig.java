package shop.bluebooktle.frontend.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/** @EnableFeignClients 를 사용하여 FeignClient를 사용하기 위한 설정 */
@Configuration
@EnableFeignClients(basePackages = {"shop.bluebooktle.frontend.repository"})
public class FeignEnableConfig {
}
