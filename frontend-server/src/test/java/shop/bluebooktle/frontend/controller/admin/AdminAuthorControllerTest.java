package shop.bluebooktle.frontend.controller.admin;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;

import shop.bluebooktle.frontend.config.advice.GlobalUserInfoAdvice;

@WebMvcTest(controllers = AdminAuthorController.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalUserInfoAdvice.class))
@ActiveProfiles("test")
public class AdminAuthorControllerTest {

}
