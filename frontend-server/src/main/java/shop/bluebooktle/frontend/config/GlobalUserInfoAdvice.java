package shop.bluebooktle.frontend.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import shop.bluebooktle.frontend.util.JwtPayloadUtil;

@ControllerAdvice
public class GlobalUserInfoAdvice {

	private static final Logger logger = LoggerFactory.getLogger(GlobalUserInfoAdvice.class);

	private static final String CLAIM_USER_ID = "sub";
	private static final String CLAIM_USER_NICKNAME = "userNickname";
	private static final String CLAIM_USER_TYPE = "userType";

	@ModelAttribute
	public void addUserInfoToModel(Model model,
		@CookieValue(name = "accessToken", required = false) String accessToken) {

		boolean isLoggedIn = false;
		String userNickname = null;
		String userType = null;

		if (StringUtils.hasText(accessToken)) {
			Map<String, Object> claims = JwtPayloadUtil.getPayloadClaims(accessToken);

			if (!claims.isEmpty()) {
				String userId = JwtPayloadUtil.getClaim(claims, CLAIM_USER_ID, String.class);

				if (StringUtils.hasText(userId)) {
					isLoggedIn = true;
					userNickname = JwtPayloadUtil.getClaim(claims, CLAIM_USER_NICKNAME, String.class);
					userType = JwtPayloadUtil.getClaim(claims, CLAIM_USER_TYPE, String.class);
					logger.debug("User identified. UserID: '{}', Nickname: '{}', Type: '{}'", userId, userNickname,
						userType);
				} else {
					logger.warn("User identifier claim ('{}') not found in token payload.", CLAIM_USER_ID);
				}
			} else {
				logger.warn(
					"Failed to parse claims from access token or claims map is empty for token starting with: [{}...]",
					accessToken.substring(0, Math.min(accessToken.length(), 10)));
			}
		}

		model.addAttribute("isLoggedIn", isLoggedIn);
		if (isLoggedIn) {
			model.addAttribute("userNickname", userNickname);
			model.addAttribute("userType", userType);
		} else {
			model.addAttribute("userNickname", null);
			model.addAttribute("userType", null);
		}
	}
}