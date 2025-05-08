package shop.bluebooktle.backend.cart.helper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import shop.bluebooktle.common.entity.User;

@UtilityClass
public class AuthContextHelper {

	public static User getUserOrNull() {
		// TODO: 인증 시스템 연동 후 SecurityContext에서 User 꺼내기
		return null; // 현재는 미구현 상태로 null 반환
	}

	public static String getGuestIdOrNull(HttpServletRequest request) {
		return request.getHeader("X-GUEST-ID");
	}
}