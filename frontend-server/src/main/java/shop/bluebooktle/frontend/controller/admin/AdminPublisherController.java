package shop.bluebooktle.frontend.controller.admin;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.request.PublisherRequest;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.frontend.service.AdminPublisherService;

@Slf4j
@Controller
// 조선대 서버에서 'publishers', 'wrappers', '-' 포함된 path 차단으로 chulpansa 사용함
@RequestMapping("/admin/chulpansa")
@RequiredArgsConstructor
public class AdminPublisherController {
	private final AdminPublisherService adminPublisherService;

	/** 출판사 목록 조회 */
	@GetMapping
	public String listPublishers(Model model, HttpServletRequest request,
		@RequestParam(value = "page", defaultValue = "0") int page, // 0-based
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword) {
		Page<PublisherInfoResponse> publisherPage = adminPublisherService.getPublishers(page, size, searchKeyword);

		log.info("어드민 출판사 목록 페이지 요청. URI: {}", request.getRequestURI());
		model.addAttribute("pageTitle", "출판사 관리");
		model.addAttribute("currentURI", request.getRequestURI());
		model.addAttribute("publishers", publisherPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", publisherPage.getTotalPages());
		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("size", size);

		return "admin/publisher/publisher_list";
	}

	/** AJAX/팝업용 — JSON 페이징 결과 반환 */
	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Page<PublisherInfoResponse> listPublishersJson(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String searchKeyword) {

		return adminPublisherService.getPublishers(page, size, searchKeyword);
	}

	/** 출판사 등록 또는 수정 폼 진입 */
	@GetMapping({"/new", "/{publisherId}/edit"})
	public String publisherForm(@PathVariable(value = "publisherId", required = false) Long publisherId,
		Model model,
		HttpServletRequest request) {
		log.info("어드민 출판사 폼 페이지 요청. URI: {}, publisherId: {}", request.getRequestURI(), publisherId);
		model.addAttribute("currentURI", request.getRequestURI());

		String pageTitle;
		PublisherInfoResponse publisher;

		if (publisherId != null) {
			publisher = adminPublisherService.getPublisher(publisherId); // 수정 시 기존 데이터 조회
			pageTitle = "출판사 수정 (ID: " + publisherId + ")";
		} else {
			pageTitle = "새 출판사 등록";
			publisher = new PublisherInfoResponse(null, "", LocalDateTime.now());
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("publisher", publisher);

		return "admin/publisher/publisher_form";
	}

	/** 출판사 저장 */
	@PostMapping("/save")
	public String savePublisher(@ModelAttribute("publisher") PublisherInfoResponse publisher,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {
		log.info("출판사 저장 요청: {}", publisher);

		if (publisher.getName() == null || publisher.getName().trim().isEmpty()) {
			bindingResult.rejectValue("name", "NotEmpty", "출판사 이름은 필수입니다.");
		}

		if (bindingResult.hasErrors()) {
			log.warn("출판사 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.publisher",
				bindingResult);
			redirectAttributes.addFlashAttribute("publisher", publisher);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");
			if (publisher.getId() != null) {
				return "redirect:/admin/chulpansa/" + publisher.getId() + "/edit";
			} else {
				return "redirect:/admin/chulpansa/new";
			}
		}

		try {
			// 실제 서비스 로직 호출 (DB에 저장/수정)
			if (publisher.getId() == null) {
				adminPublisherService.createPublisher(new PublisherRequest(publisher.getName()));
			} else {
				adminPublisherService.updatePublisher(publisher.getId(),
					new PublisherRequest(publisher.getName())); // name 만 수정
			}
			String action = (publisher.getId() == null) ? "등록" : "수정";
			log.info("출판사 {} 처리 (임시): Name={}, DeletedAt={}", action, publisher.getName());
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"출판사 '" + publisher.getName() + "'가 성공적으로 " + action + "되었습니다.");
		} catch (Exception e) {
			log.error("출판사 저장 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "출판사 저장 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("publisher", publisher);
			if (publisher.getId() != null) {
				return "redirect:/admin/chulpansa/" + publisher.getId() + "/edit";
			} else {
				return "redirect:/admin/chulpansa/new";
			}
		}
		return "redirect:/admin/chulpansa";
	}

	@PostMapping("/{publisherId}/delete") // 비활성화
	public String deletePublisher(@PathVariable Long publisherId, RedirectAttributes redirectAttributes) {
		log.info("출판사 비활성화 요청: ID {}", publisherId);
		try {
			adminPublisherService.deletePublisher(publisherId);
			log.info("임시 출판사 비활성화 성공 처리: ID {}", publisherId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"출판사(ID: " + publisherId + ")가 성공적으로 비활성화 처리되었습니다.");
		} catch (Exception e) {
			log.error("출판사 비활성화 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "출판사 비활성화 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/chulpansa";
	}
}
