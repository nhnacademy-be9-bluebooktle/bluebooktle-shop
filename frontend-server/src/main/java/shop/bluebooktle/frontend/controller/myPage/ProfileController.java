package shop.bluebooktle.frontend.controller.myPage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.frontend.service.UserService;

@Controller
@RequestMapping("/mypage/profile")
@RequiredArgsConstructor
public class ProfileController {

	private final UserService userService;

	private String convertToDisplayFormat(String yyyymmdd) {
		if (StringUtils.hasText(yyyymmdd) && yyyymmdd.length() == 8) {
			try {
				return yyyymmdd.substring(0, 4) + "-" + yyyymmdd.substring(4, 6) + "-" + yyyymmdd.substring(6, 8);
			} catch (Exception e) {
				return yyyymmdd;
			}
		}
		return yyyymmdd;
	}

	private String convertToDbFormat(String yyyy_mm_dd) {
		if (StringUtils.hasText(yyyy_mm_dd) && yyyy_mm_dd.contains("-")) {
			return yyyy_mm_dd.replace("-", "");
		}
		return yyyy_mm_dd;
	}

	@GetMapping("")
	public String userProfilePage(Model model, RedirectAttributes redirectAttributes) {
		try {
			UserResponse user = userService.getMe();
			model.addAttribute("user", user);

			if (!model.containsAttribute("UserUpdateRequest")) {
				UserUpdateRequest dto = new UserUpdateRequest();
				if (user != null) {
					dto.setNickname(user.getNickname());
					dto.setPhoneNumber(user.getPhoneNumber());
					dto.setBirthDate(convertToDisplayFormat(user.getBirth()));
				}
				model.addAttribute("UserUpdateRequest", dto);
			}
		} catch (ApplicationException e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", e.getErrorCode().getMessage());
			redirectAttributes.addFlashAttribute("globalErrorTitle", e.getErrorCode().getCode());
			return "redirect:/";
		}
		return "mypage/profile";
	}

	@PostMapping("/{id}")
	public String updateUserProfile(@PathVariable Long id,
		@Valid @ModelAttribute("UserUpdateRequest") UserUpdateRequest userUpdateRequest, // 모델 객체 이름 명시
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.UserUpdateRequest",
				bindingResult);
			redirectAttributes.addFlashAttribute("UserUpdateRequest", userUpdateRequest);

			return "redirect:/mypage/profile";
		}

		String birthDateForDb = convertToDbFormat(userUpdateRequest.getBirthDate());
		userUpdateRequest.setBirthDate(birthDateForDb);

		try {
			userService.updateUser(id, userUpdateRequest);

			redirectAttributes.addFlashAttribute("globalSuccessMessage", "프로필이 업데이트되었습니다");
			return "redirect:/mypage/profile";

		} catch (ApplicationException e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", e.getErrorCode().getMessage());
			redirectAttributes.addFlashAttribute("globalErrorTitle", e.getErrorCode().getCode());
			userUpdateRequest.setBirthDate(convertToDisplayFormat(userUpdateRequest.getBirthDate()));
			redirectAttributes.addFlashAttribute("UserUpdateRequest", userUpdateRequest);
			return "redirect:/mypage/profile";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "프로필 업데이트 중 오류가 발생했습니다: " + e.getMessage());
			userUpdateRequest.setBirthDate(convertToDisplayFormat(userUpdateRequest.getBirthDate()));
			redirectAttributes.addFlashAttribute("UserUpdateRequest", userUpdateRequest);
			return "redirect:/mypage/profile";
		}
	}
}