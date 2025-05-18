package shop.bluebooktle.frontend.controller.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

	// --- MemberDto: 상세 정보 표시를 위해 필드 확장 ---
	@Getter
	@Setter
	@ToString
	static class MemberDto { // 기존 MemberDto를 상세 정보용으로 확장하거나, 별도의 MemberDetailViewDto를 만들 수 있음
		private Long userId;
		private String loginId;
		private String name;
		private String nickname;
		private String email;
		private String phoneNumber;
		private String birthDate; // 스키마는 birth (varchar(8))
		private BigDecimal pointBalance;
		private String userType; // 스키마는 type enum('USER', 'ADMIN')
		private String userStatus; // 스키마는 status enum('ACTIVE', 'DORMANT', 'WITHDRAWN')
		private String membershipName; // membership_level.name
		private Long membershipId; // 추가: 등급 정책 연결용
		private LocalDateTime lastLoginAt;
		private LocalDateTime createdAt;
		private LocalDateTime deletedAt; // 탈퇴/삭제 여부 확인용

		// 연관 정보 (임시 목록 또는 실제 데이터)
		private List<AddressDto> addresses = new ArrayList<>();
		private List<PointHistoryDto> pointHistories = new ArrayList<>();
		private List<UserCouponDto> userCoupons = new ArrayList<>();
		private List<OrderSummaryDto> orderSummaries = new ArrayList<>();

		public MemberDto() {
		}

		public MemberDto(Long userId, String loginId, String name, String nickname, String email, String phoneNumber,
			String birthDate, BigDecimal pointBalance, String userType, String userStatus,
			String membershipName, Long membershipId, LocalDateTime lastLoginAt, LocalDateTime createdAt,
			LocalDateTime deletedAt) {
			this.userId = userId;
			this.loginId = loginId;
			this.name = name;
			this.nickname = nickname;
			this.email = email;
			this.phoneNumber = phoneNumber;
			this.birthDate = birthDate;
			this.pointBalance = pointBalance;
			this.userType = userType;
			this.userStatus = userStatus;
			this.membershipName = membershipName;
			this.membershipId = membershipId;
			this.lastLoginAt = lastLoginAt;
			this.createdAt = createdAt;
			this.deletedAt = deletedAt;
		}
	}

	// --- 연관 정보 DTO (임시) ---
	@Getter
	@Setter
	@ToString
	static class AddressDto {
		private Long addressId;
		private String alias;
		private String roadAddress;
		private String detailAddress;
		private String postalCode;
		private boolean isDefault; // 기본 배송지 여부 (스키마에는 없음, UI 편의상 추가 가능)

		public AddressDto(Long addressId, String alias, String roadAddress, String detailAddress, String postalCode,
			boolean isDefault) {
			this.addressId = addressId;
			this.alias = alias;
			this.roadAddress = roadAddress;
			this.detailAddress = detailAddress;
			this.postalCode = postalCode;
			this.isDefault = isDefault;
		}
	}

	@Getter
	@Setter
	@ToString
	static class PointHistoryDto {
		private Long pointId;
		private String sourceType; // point_source_type.source_type
		private BigDecimal value;
		private LocalDateTime createdAt;

		public PointHistoryDto(Long pointId, String sourceType, BigDecimal value, LocalDateTime createdAt) {
			this.pointId = pointId;
			this.sourceType = sourceType;
			this.value = value;
			this.createdAt = createdAt;
		}
	}

	@Getter
	@Setter
	@ToString
	static class UserCouponDto {
		private Long userCouponId;
		private String couponName; // coupon.name
		private LocalDateTime availableStartAt;
		private LocalDateTime availableEndAt;
		private LocalDateTime usedAt;

		public UserCouponDto(Long userCouponId, String couponName, LocalDateTime availableStartAt,
			LocalDateTime availableEndAt, LocalDateTime usedAt) {
			this.userCouponId = userCouponId;
			this.couponName = couponName;
			this.availableStartAt = availableStartAt;
			this.availableEndAt = availableEndAt;
			this.usedAt = usedAt;
		}
	}

	@Getter
	@Setter
	@ToString
	static class OrderSummaryDto {
		private Long orderId;
		private LocalDateTime orderDate;
		private String orderState; // order_state.state
		private BigDecimal totalAmount; // 계산 필요

		public OrderSummaryDto(Long orderId, LocalDateTime orderDate, String orderState, BigDecimal totalAmount) {
			this.orderId = orderId;
			this.orderDate = orderDate;
			this.orderState = orderState;
			this.totalAmount = totalAmount;
		}
	}

	// --- MemberFormDto: 수정용 DTO ---
	@Getter
	@Setter
	@ToString
	static class MemberFormDto { // 기존 MemberFormDto 유지 또는 확장
		private Long userId;
		// loginId는 보통 수정 불가 항목이지만, 필요에 따라 포함. 여기서는 조회용으로만.
		private String loginId; // 수정 불가, 조회용
		private String name;
		private String nickname;
		private String email;
		private String phoneNumber;
		private String birthDate; // 추가: 스키마의 birth (varchar(8))
		// private String password; // 비밀번호 변경은 별도 기능으로 분리하는 것이 일반적
		private String userType; // enum('USER', 'ADMIN')
		private String userStatus; // enum('ACTIVE', 'DORMANT', 'WITHDRAWN')
		private Long membershipId; // 추가: 등급 변경용

		// 유효성 검사를 위한 어노테이션 추가 가능 (예: @NotBlank, @Email, @Pattern)
		// 예: import jakarta.validation.constraints.NotBlank;
		// @NotBlank(message = "이름은 필수입니다.") private String name;

		public MemberFormDto() {
		}
	}

	// --- 데모 데이터 리스트 ---
	private static final List<MemberDto> allMembersForDemo = new ArrayList<>();
	private static final List<AddressDto> demoAddressesUser1 = new ArrayList<>();
	private static final List<PointHistoryDto> demoPointHistoriesUser1 = new ArrayList<>();
	private static final List<UserCouponDto> demoUserCouponsUser1 = new ArrayList<>();
	private static final List<OrderSummaryDto> demoOrderSummariesUser1 = new ArrayList<>();

	// --- 정적 초기화 블록: 데모 데이터 생성 ---
	static {
		// 사용자 1의 상세 정보용 데모 데이터
		demoAddressesUser1.add(new AddressDto(1L, "집", "서울시 강남구 테헤란로 123", "삼성빌딩 5층", "06123", true));
		demoAddressesUser1.add(new AddressDto(2L, "회사", "경기도 성남시 분당구 판교역로 456", "알파타워 10층", "13529", false));
		demoPointHistoriesUser1.add(new PointHistoryDto(1L, "회원가입 축하", new BigDecimal("1000.00"),
			LocalDateTime.now().minusMonths(2).minusDays(1)));
		demoPointHistoriesUser1.add(new PointHistoryDto(2L, "도서 구매 ('스프링 부트 따라하기')", new BigDecimal("150.00"),
			LocalDateTime.now().minusMonths(1)));
		demoPointHistoriesUser1.add(
			new PointHistoryDto(3L, "리뷰 작성 보상", new BigDecimal("50.00"), LocalDateTime.now().minusDays(10)));
		demoPointHistoriesUser1.add(
			new PointHistoryDto(4L, "포인트 사용 ('쿠폰 구매')", new BigDecimal("-500.00"), LocalDateTime.now().minusDays(5)));
		demoUserCouponsUser1.add(new UserCouponDto(1L, "신규회원 10% 할인쿠폰", LocalDateTime.now().minusMonths(2),
			LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusMonths(1).plusDays(5)));
		demoUserCouponsUser1.add(new UserCouponDto(2L, "생일축하 2000원 할인쿠폰", LocalDateTime.now().minusDays(10),
			LocalDateTime.now().plusDays(20), null));
		demoOrderSummariesUser1.add(
			new OrderSummaryDto(1001L, LocalDateTime.now().minusMonths(1), "COMPLETED", new BigDecimal("35000.00")));
		demoOrderSummariesUser1.add(
			new OrderSummaryDto(1005L, LocalDateTime.now().minusDays(5), "SHIPPING", new BigDecimal("22000.00")));

		// 전체 회원 목록 데모 데이터
		MemberDto user1 = new MemberDto(1L, "user01", "김회원", "별명하나", "user01@example.com", "01012345678", "19900101",
			new BigDecimal("1500.00"), "USER", "ACTIVE", "BRONZE", 1L, LocalDateTime.now().minusHours(5),
			LocalDateTime.now().minusMonths(2), null);
		user1.setAddresses(demoAddressesUser1);
		user1.setPointHistories(demoPointHistoriesUser1);
		user1.setUserCoupons(demoUserCouponsUser1);
		user1.setOrderSummaries(demoOrderSummariesUser1);
		allMembersForDemo.add(user1);

		allMembersForDemo.add(
			new MemberDto(2L, "admin01", "박관리", "관리자팍", "admin01@example.com", "01087654321", "19850515",
				new BigDecimal("0.00"), "ADMIN", "ACTIVE", "GOLD", 3L, LocalDateTime.now().minusMinutes(30),
				LocalDateTime.now().minusYears(1), null));
		allMembersForDemo.add(
			new MemberDto(3L, "user02", "이휴면", "잠자는회원", "user02_dormant@example.com", "01011112222", "20001225",
				new BigDecimal("500.00"), "USER", "DORMANT", "SILVER", 2L, LocalDateTime.now().minusMonths(7),
				LocalDateTime.now().minusMonths(8), null));
		for (int i = 4; i <= 25; i++) {
			allMembersForDemo.add(new MemberDto((long)i, "user" + String.format("%02d", i), "테스트사용자" + i, "닉네임" + i,
				"user" + i + "@example.com", "010123456" + String.format("%02d", i), "19950101",
				new BigDecimal(i * 100), "USER", (i % 3 == 0) ? "ACTIVE" : (i % 3 == 1) ? "DORMANT" : "WITHDRAWN",
				(i % 2 == 0) ? "BRONZE" : "SILVER", (i % 2 == 0) ? 1L : 2L, LocalDateTime.now().minusDays(i),
				LocalDateTime.now().minusMonths(i), (i % 5 == 0) ? LocalDateTime.now().minusDays(i / 2) : null));
		}
	}

	// 회원 등급 목록 (수정 폼에서 선택용)
	// 실제로는 서비스에서 조회
	private List<MembershipLevelDto> getDemoMembershipLevels() {
		return Arrays.asList(
			new MembershipLevelDto(1L, "BRONZE"),
			new MembershipLevelDto(2L, "SILVER"),
			new MembershipLevelDto(3L, "GOLD")
		);
	}

	@Getter
	@Setter
	static class MembershipLevelDto {
		private Long id;
		private String name;

		public MembershipLevelDto(Long id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	// --- 회원 목록 조회 ---
	@GetMapping
	public String listMembers(Model model, HttpServletRequest request,
		// ... (기존 listMembers 메소드 내용은 이전 답변 참고하여 유지) ...
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchField", required = false) String searchField,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
		@RequestParam(value = "userTypeFilter", required = false) String userTypeFilter,
		@RequestParam(value = "userStatusFilter", required = false) String userStatusFilter) {

		log.info(
			"AdminMemberController - listMembers: page={}, size={}, searchField={}, searchKeyword={}, userTypeFilter={}, userStatusFilter={}",
			page, size, searchField, searchKeyword, userTypeFilter, userStatusFilter);
		model.addAttribute("pageTitle", "회원 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		Pageable pageable = PageRequest.of(page, size, sort);

		List<MemberDto> filteredMembers = allMembersForDemo.stream()
			.filter(m -> (searchKeyword == null || searchKeyword.trim().isEmpty()) ||
				(searchField == null || searchField.trim().isEmpty()) ||
				(searchField.equals("loginId") && m.getLoginId() != null && m.getLoginId()
					.toLowerCase()
					.contains(searchKeyword.toLowerCase())) ||
				(searchField.equals("name") && m.getName() != null && m.getName()
					.toLowerCase()
					.contains(searchKeyword.toLowerCase())) ||
				(searchField.equals("nickname") && m.getNickname() != null && m.getNickname()
					.toLowerCase()
					.contains(searchKeyword.toLowerCase())) ||
				(searchField.equals("email") && m.getEmail() != null && m.getEmail()
					.toLowerCase()
					.contains(searchKeyword.toLowerCase())))
			.filter(
				m -> (userTypeFilter == null || userTypeFilter.isEmpty()) || (m.getUserType() != null && m.getUserType()
					.equals(userTypeFilter)))
			.filter(m -> (userStatusFilter == null || userStatusFilter.isEmpty()) || (m.getUserStatus() != null
				&& m.getUserStatus().equals(userStatusFilter)))
			.collect(Collectors.toList());

		if (pageable.getSort().isSorted()) {
			pageable.getSort().forEach(order -> {
				if (order.getProperty().equals("createdAt")) {
					filteredMembers.sort((m1, m2) -> order.isAscending() ?
						(m1.getCreatedAt() == null ? -1 :
							m2.getCreatedAt() == null ? 1 : m1.getCreatedAt().compareTo(m2.getCreatedAt())) :
						(m1.getCreatedAt() == null ? 1 :
							m2.getCreatedAt() == null ? -1 : m2.getCreatedAt().compareTo(m1.getCreatedAt()))
					);
				}
			});
		}

		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), filteredMembers.size());
		List<MemberDto> pageContent =
			(start >= filteredMembers.size() || start > end) ? List.of() : filteredMembers.subList(start, end);
		Page<MemberDto> membersPage = new PageImpl<>(pageContent, pageable, filteredMembers.size());

		model.addAttribute("members", membersPage.getContent());
		model.addAttribute("currentPage", membersPage.getNumber());
		model.addAttribute("totalPages", membersPage.getTotalPages());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(request.getRequestURI())
			.queryParam("size", membersPage.getSize());
		if (searchField != null && !searchField.isEmpty())
			uriBuilder.queryParam("searchField", searchField);
		if (searchKeyword != null && !searchKeyword.isEmpty())
			uriBuilder.queryParam("searchKeyword", searchKeyword);
		if (userTypeFilter != null && !userTypeFilter.isEmpty())
			uriBuilder.queryParam("userTypeFilter", userTypeFilter);
		if (userStatusFilter != null && !userStatusFilter.isEmpty())
			uriBuilder.queryParam("userStatusFilter", userStatusFilter);

		String baseUrlWithParams = uriBuilder.toUriString();
		model.addAttribute("baseUrlWithParams", baseUrlWithParams);

		model.addAttribute("searchField", searchField);
		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("userTypeFilter", userTypeFilter);
		model.addAttribute("userStatusFilter", userStatusFilter);

		model.addAttribute("userTypeOptions", Arrays.asList("USER", "ADMIN"));
		model.addAttribute("userStatusOptions", Arrays.asList("ACTIVE", "DORMANT", "WITHDRAWN"));
		model.addAttribute("allMembersForDemo", allMembersForDemo); // member_list.html 에서 총 건수 표시용 임시 추가

		return "admin/member/member_list";
	}

	// --- 회원 상세 정보 조회 ---
	@GetMapping("/{userId}")
	public String memberDetail(@PathVariable Long userId, Model model, HttpServletRequest request) {
		log.info("AdminMemberController - memberDetail: userId={}", userId);
		model.addAttribute("currentURI", request.getRequestURI());

		// TODO: 실제 서비스 로직으로 교체 (memberService.findMemberDetailById(userId))
		Optional<MemberDto> memberOpt = allMembersForDemo.stream()
			.filter(m -> m.getUserId().equals(userId))
			.findFirst();

		if (memberOpt.isEmpty()) {
			log.warn("Member not found for ID: {}", userId);
			model.addAttribute("pageTitle", "회원 정보 없음");
			model.addAttribute("globalErrorMessage", "해당 ID의 회원 정보를 찾을 수 없습니다: " + userId);
			// 적절한 에러 페이지로 리다이렉트 하거나, 목록 페이지로 리다이렉트와 함께 메시지 전달
			// return "redirect:/admin/members?error=MemberNotFound";
			return "admin/error/resource_not_found"; // 예시 에러 페이지
		}

		MemberDto member = memberOpt.get();
		model.addAttribute("pageTitle", "회원 상세 정보: " + member.getName() + " (" + member.getLoginId() + ")");
		model.addAttribute("member", member);

		// (선택) 필요한 경우, 이 페이지에서 바로 상태 변경 등의 작업을 할 수 있도록 관련 정보 추가
		model.addAttribute("userStatusOptions", Arrays.asList("ACTIVE", "DORMANT", "WITHDRAWN"));

		return "admin/member/member_detail";
	}

	// --- 회원 정보 수정 폼 ---
	@GetMapping("/{userId}/edit")
	public String memberEditForm(@PathVariable Long userId, Model model, HttpServletRequest request) {
		log.info("AdminMemberController - memberEditForm: userId={}", userId);
		model.addAttribute("currentURI", request.getRequestURI());

		// TODO: 실제 서비스 로직으로 교체 (memberService.findMemberById(userId) -> MemberFormDto 변환)
		Optional<MemberDto> memberOpt = allMembersForDemo.stream()
			.filter(m -> m.getUserId().equals(userId))
			.findFirst();

		if (memberOpt.isEmpty()) {
			log.warn("Member not found for edit ID: {}", userId);
			// RedirectAttributes 사용하여 메시지 전달 후 목록으로 리다이렉트
			// redirectAttributes.addFlashAttribute("globalErrorMessage", "수정할 회원 정보를 찾을 수 없습니다: " + userId);
			// return "redirect:/admin/members";
			model.addAttribute("pageTitle", "회원 정보 없음");
			model.addAttribute("globalErrorMessage", "수정할 회원 정보를 찾을 수 없습니다: " + userId);
			return "admin/error/resource_not_found";
		}

		MemberDto memberDto = memberOpt.get();
		MemberFormDto memberFormDto = new MemberFormDto();
		memberFormDto.setUserId(memberDto.getUserId());
		memberFormDto.setLoginId(memberDto.getLoginId()); // 수정 불가 항목으로 폼에 표시만
		memberFormDto.setName(memberDto.getName());
		memberFormDto.setNickname(memberDto.getNickname());
		memberFormDto.setEmail(memberDto.getEmail());
		memberFormDto.setPhoneNumber(memberDto.getPhoneNumber());
		memberFormDto.setBirthDate(memberDto.getBirthDate());
		memberFormDto.setUserType(memberDto.getUserType());
		memberFormDto.setUserStatus(memberDto.getUserStatus());
		memberFormDto.setMembershipId(memberDto.getMembershipId());

		model.addAttribute("pageTitle", "회원 정보 수정: " + memberDto.getName());
		model.addAttribute("memberForm", memberFormDto); // th:object 바인딩용
		model.addAttribute("userTypeOptions", Arrays.asList("USER", "ADMIN")); // ENUM 값들
		model.addAttribute("userStatusOptions", Arrays.asList("ACTIVE", "DORMANT", "WITHDRAWN"));
		model.addAttribute("membershipLevels", getDemoMembershipLevels()); // 회원 등급 선택 목록

		return "admin/member/member_form";
	}

	// --- 회원 정보 수정 처리 ---
	@PostMapping("/{userId}/edit") // 또는 @PostMapping("/update") 등 일관성 있는 URL 사용
	public String updateMember(@PathVariable Long userId,
		@Valid @ModelAttribute("memberForm") MemberFormDto memberFormDto,
		BindingResult bindingResult, // @Valid 결과
		Model model, // 유효성 검사 실패 시 다시 폼을 보여주기 위해
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {

		log.info("AdminMemberController - updateMember: userId={}, formDto={}", userId, memberFormDto);

		// PathVariable의 userId와 formDto의 userId가 일치하는지 확인 (보안 강화)
		if (!userId.equals(memberFormDto.getUserId())) {
			log.warn("Path variable userId {} does not match form userId {}", userId, memberFormDto.getUserId());
			redirectAttributes.addFlashAttribute("globalErrorMessage", "잘못된 요청입니다.");
			return "redirect:/admin/members";
		}

		// 추가적인 서버 사이드 유효성 검사 (BindingResult 외)
		// 예: loginId 중복 검사 (수정은 아니지만, 만약 수정 가능하게 한다면)
		// 예: 닉네임 중복 검사 (다른 사용자와 중복되지 않도록)
		// if (memberService.isNicknameTakenByOtherUser(userId, memberFormDto.getNickname())) {
		//    bindingResult.rejectValue("nickname", "Duplicate.memberForm.nickname", "이미 사용 중인 닉네임입니다.");
		// }

		if (bindingResult.hasErrors()) {
			log.warn("Validation errors on member update: {}", bindingResult.getAllErrors());
			// 에러 메시지와 함께 수정 폼으로 다시 이동 (리다이렉트가 아닌 forward)
			// 이렇게 하면 입력 값과 에러 메시지가 유지됨
			model.addAttribute("pageTitle", "회원 정보 수정 (오류): " + memberFormDto.getName());
			model.addAttribute("userTypeOptions", Arrays.asList("USER", "ADMIN"));
			model.addAttribute("userStatusOptions", Arrays.asList("ACTIVE", "DORMANT", "WITHDRAWN"));
			model.addAttribute("membershipLevels", getDemoMembershipLevels());
			// currentURI는 GET 요청 시 설정되므로, 여기서 다시 설정하거나 layout에서 처리
			model.addAttribute("currentURI", request.getRequestURI());
			return "admin/member/member_form"; // 리다이렉트 없이 같은 뷰로 돌아가 에러 표시
		}

		try {
			// TODO: 실제 서비스 로직 호출 (memberService.updateMember(userId, memberFormDto))
			// 데모 데이터 업데이트 (실제로는 DB 업데이트)
			allMembersForDemo.stream()
				.filter(m -> m.getUserId().equals(userId))
				.findFirst()
				.ifPresent(memberToUpdate -> {
					memberToUpdate.setName(memberFormDto.getName());
					memberToUpdate.setNickname(memberFormDto.getNickname());
					memberToUpdate.setEmail(memberFormDto.getEmail());
					memberToUpdate.setPhoneNumber(memberFormDto.getPhoneNumber());
					memberToUpdate.setBirthDate(memberFormDto.getBirthDate());
					memberToUpdate.setUserType(memberFormDto.getUserType());
					memberToUpdate.setUserStatus(memberFormDto.getUserStatus());
					memberToUpdate.setMembershipId(memberFormDto.getMembershipId());
					// membershipName도 업데이트 필요 (membershipId에 따라)
					Optional<MembershipLevelDto> levelOpt = getDemoMembershipLevels().stream()
						.filter(level -> level.getId().equals(memberFormDto.getMembershipId()))
						.findFirst();
					levelOpt.ifPresent(levelDto -> memberToUpdate.setMembershipName(levelDto.getName()));
				});

			log.info("Member updated successfully for ID: {}", userId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "회원(ID: " + userId + ") 정보가 성공적으로 수정되었습니다.");
			return "redirect:/admin/members/" + userId; // 수정 후 상세 페이지로 리다이렉트
		} catch (Exception e) { // 실제로는 구체적인 예외 처리
			log.error("Error updating member for ID: " + userId, e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "회원 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("memberForm", memberFormDto); // 입력값 유지
			// 오류 발생 시에도 수정 폼으로 리다이렉트하여 메시지 표시
			return "redirect:/admin/members/" + userId + "/edit";
		}
	}
}