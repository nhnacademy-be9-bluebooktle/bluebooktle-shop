package shop.bluebooktle.frontend.config;

import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import shop.bluebooktle.frontend.util.JwtPayloadUtil;

@ControllerAdvice
public class GlobalUserInfoAdvice {

	private static final String CLAIM_USER_ID = "sub";
	private static final String CLAIM_USER_NICKNAME = "userNickname";
	private static final String CLAIM_USER_TYPE = "userType";

	@ModelAttribute
	public void addUserInfoToModel(Model model,
		@CookieValue(name = "accessToken", required = false) String accessToken) {

		boolean isLoggedIn = false;
		Long userId = null;
		String userNickname = null;
		String userType = null;

		if (StringUtils.hasText(accessToken)) {
			Map<String, Object> claims = JwtPayloadUtil.getPayloadClaims(accessToken);
			isLoggedIn = true;

			userNickname = JwtPayloadUtil.getClaim(claims, CLAIM_USER_NICKNAME, String.class);
			userType = JwtPayloadUtil.getClaim(claims, CLAIM_USER_TYPE, String.class);
		}

		model.addAttribute("isLoggedIn", isLoggedIn);
		if (isLoggedIn) {
			model.addAttribute("userId", userId);
			model.addAttribute(CLAIM_USER_NICKNAME, userNickname);
			model.addAttribute(CLAIM_USER_TYPE, userType);
		} else {
			model.addAttribute("userId", null);
			model.addAttribute(CLAIM_USER_NICKNAME, null);
			model.addAttribute(CLAIM_USER_TYPE, null);
		}
	}
}