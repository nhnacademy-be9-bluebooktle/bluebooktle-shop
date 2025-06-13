package shop.bluebooktle.frontend.controller.admin;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.membership.MembershipLevelDto;
import shop.bluebooktle.common.dto.user.request.AdminUserUpdateRequest;
import shop.bluebooktle.common.dto.user.request.UserSearchRequest;
import shop.bluebooktle.common.dto.user.response.AdminUserResponse;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.frontend.service.UserService;

@Slf4j
@Controller
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

	private final UserService userService;

	private List<MembershipLevelDto> getDemoMembershipLevels() {
		return Arrays.asList(
			new MembershipLevelDto(1L, "BRONZE"),
			new MembershipLevelDto(2L, "SILVER"),
			new MembershipLevelDto(3L, "GOLD")
		);
	}

	@GetMapping
	public String listMembers(Model model, HttpServletRequest request,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@ModelAttribute UserSearchRequest searchRequest) {

		model.addAttribute("pageTitle", "회원 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		int zeroBasedPage = (page < 0) ? 0 : page;
		Pageable pageable = PageRequest.of(zeroBasedPage, size, Sort.by(Sort.Direction.DESC, "id"));

		Page<AdminUserResponse> membersPage = userService.listUsers(searchRequest, pageable);

		model.addAttribute("members", membersPage.getContent());
		model.addAttribute("currentPage", membersPage.getNumber());
		model.addAttribute("totalPages", membersPage.getTotalPages());
		model.addAttribute("totalMembers", membersPage.getTotalElements());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(request.getRequestURI())
			.queryParam("size", membersPage.getSize());
		if (searchRequest.getSearchField() != null && !searchRequest.getSearchField().isEmpty())
			uriBuilder.queryParam("searchField", searchRequest.getSearchField());
		if (searchRequest.getSearchKeyword() != null && !searchRequest.getSearchKeyword().isEmpty())
			uriBuilder.queryParam("searchKeyword", searchRequest.getSearchKeyword());
		if (searchRequest.getUserTypeFilter() != null && !searchRequest.getUserTypeFilter().isEmpty())
			uriBuilder.queryParam("userTypeFilter", searchRequest.getUserTypeFilter());
		if (searchRequest.getUserStatusFilter() != null && !searchRequest.getUserStatusFilter().isEmpty())
			uriBuilder.queryParam("userStatusFilter", searchRequest.getUserStatusFilter());

		model.addAttribute("baseUrlWithParams", uriBuilder.toUriString());

		model.addAttribute("searchField", searchRequest.getSearchField());
		model.addAttribute("searchKeyword", searchRequest.getSearchKeyword());
		model.addAttribute("userTypeFilter", searchRequest.getUserTypeFilter());
		model.addAttribute("userStatusFilter", searchRequest.getUserStatusFilter());

		model.addAttribute("userTypeOptions", Arrays.stream(UserType.values()).map(Enum::name).toList());
		model.addAttribute("userStatusOptions", Arrays.stream(UserStatus.values()).map(Enum::name).toList());

		return "admin/member/member_list";
	}

	@GetMapping("/{userId}")
	public String memberDetail(@PathVariable Long userId, Model model, HttpServletRequest request,
		RedirectAttributes redirectAttributes) {
		log.info("AdminMemberController - memberDetail: userId={}", userId);
		model.addAttribute("currentURI", request.getRequestURI());

		try {
			AdminUserResponse member = userService.getUserDetail(userId);
			model.addAttribute("pageTitle", "회원 상세 정보: " + member.getName() + " (" + member.getLoginId() + ")");
			model.addAttribute("member", member);
			return "admin/member/member_detail";

		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", e.getMessage());
			return "redirect:/admin/members";
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "회원 정보를 불러오는 중 오류가 발생했습니다.");
			return "redirect:/admin/members";
		}
	}

	@GetMapping("/{userId}/edit")
	public String memberEditForm(@PathVariable Long userId, Model model, HttpServletRequest request,
		RedirectAttributes redirectAttributes) {
		log.info("AdminMemberController - memberEditForm: userId={}", userId);
		model.addAttribute("currentURI", request.getRequestURI());

		try {
			AdminUserResponse user = userService.getUserDetail(userId);
			AdminUserUpdateRequest formDto = mapToUpdateRequest(user);

			model.addAttribute("pageTitle", "회원 정보 수정: " + user.getName());
			model.addAttribute("memberForm", formDto);
			model.addAttribute("userId", userId);
			model.addAttribute("loginId", user.getLoginId());
			addFormOptionsToModel(model);

			return "admin/member/member_form";

		} catch (UserNotFoundException e) {
			log.warn("Cannot show edit form for ID {}: {}", userId, e.getMessage());
			redirectAttributes.addFlashAttribute("globalErrorMessage", "수정할 회원 정보를 찾을 수 없습니다: " + e.getMessage());
			return "redirect:/admin/members";
		} catch (RuntimeException e) {
			log.error("Error fetching user for edit: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("globalErrorMessage", "회원 정보를 불러오는 중 오류가 발생했습니다.");
			return "redirect:/admin/members";
		}
	}

	@PostMapping("/{userId}/edit")
	public String updateMember(@PathVariable Long userId,
		@Valid @ModelAttribute("memberForm") AdminUserUpdateRequest memberForm,
		BindingResult bindingResult,
		Model model,
		RedirectAttributes redirectAttributes) {

		log.info("AdminMemberController - updateMember: userId={}, formDto={}", userId, memberForm);

		if (bindingResult.hasErrors()) {
			log.warn("Validation errors on member update: {}", bindingResult.getAllErrors());
			model.addAttribute("pageTitle", "회원 정보 수정 (오류)");
			addFormOptionsToModel(model);
			model.addAttribute("userId", userId);
			model.addAttribute("loginId", "ID 조회 필요");
			model.addAttribute("globalErrorMessage", "입력 값을 확인해주세요.");
			return "admin/member/member_form";
		}

		try {
			userService.updateUser(userId, memberForm);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "회원(ID: " + userId + ") 정보가 성공적으로 수정되었습니다.");
			return "redirect:/admin/members/" + userId;

		} catch (RuntimeException e) {
			log.error("Error updating member for ID {}: {}", userId, e.getMessage());
			model.addAttribute("pageTitle", "회원 정보 수정 (오류)");
			addFormOptionsToModel(model);
			model.addAttribute("userId", userId);
			model.addAttribute("loginId", "ID 조회 필요");
			model.addAttribute("globalErrorMessage", "회원 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
			return "admin/member/member_form";
		}
	}

	private AdminUserUpdateRequest mapToUpdateRequest(AdminUserResponse user) {
		AdminUserUpdateRequest dto = new AdminUserUpdateRequest();
		dto.setName(user.getName());
		dto.setNickname(user.getNickname());
		dto.setEmail(user.getEmail());
		dto.setPhoneNumber(user.getPhoneNumber());
		dto.setBirthDate(user.getBirthDate());
		dto.setUserType(user.getUserType());
		dto.setUserStatus(user.getUserStatus());
		dto.setMembershipId(user.getMembershipId());
		return dto;
	}

	private void addFormOptionsToModel(Model model) {
		model.addAttribute("userTypeOptions", Arrays.stream(UserType.values()).toList());
		model.addAttribute("userStatusOptions", Arrays.stream(UserStatus.values()).toList());
		model.addAttribute("membershipLevels", getDemoMembershipLevels());
	}
}