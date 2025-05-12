package shop.bluebooktle.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

	@GetMapping("/login")
	public String loginForm() {
		return "auth/login_form";
	}

	@GetMapping("/signup")
	public String joinForm() {
		return "auth/signup_form";
	}
}