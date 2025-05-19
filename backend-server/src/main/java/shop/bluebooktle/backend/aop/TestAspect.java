package shop.bluebooktle.backend.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TestAspect {

	@Before("execution(* shop.bluebooktle.backend.user.controller.UserController.*(..))") // UserController의 모든 메소드 실행 전
	public void logBeforeUserControllerMethods() {
		System.out.println("!!!!!!!!!! TestAspect: UserController method is about to be called !!!!!!!!!!");
	}
}