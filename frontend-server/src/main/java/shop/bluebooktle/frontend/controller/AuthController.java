package shop.bluebooktle.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

	@GetMapping("/login")
	public String loginForm() {
		return "login_form";
	}

	@GetMapping("/signup")
	public String joinForm() {
		return "signup_form";
	}

	@GetMapping("/findId")
	public String findIdForm() {
		return "auth/findIdForm";
	}

	@GetMapping("/findPassword")
	public String findPasswordForm() {
		return "auth/findPasswordForm";
	}

}