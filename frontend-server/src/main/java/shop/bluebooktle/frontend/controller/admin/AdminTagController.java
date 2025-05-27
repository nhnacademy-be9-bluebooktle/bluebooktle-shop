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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.request.TagRequest;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.frontend.service.AdminTagService;

@Slf4j
@Controller
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

	private final AdminTagService tagService;

	/** 태그 목록 조회 */
	@GetMapping
	public String listTags(Model model, HttpServletRequest request,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword) {
		Page<TagInfoResponse> tagPage = tagService.getTags(page, size, searchKeyword);

		log.info("어드민 태그 목록 페이지 요청. URI: {}", request.getRequestURI());
		model.addAttribute("pageTitle", "태그 관리");
		model.addAttribute("currentURI", request.getRequestURI());
		model.addAttribute("tags", tagPage.getContent()); // 태그 리스트
		model.addAttribute("currentPage", page); // 현재 페이지 번호
		model.addAttribute("totalPages", tagPage.getTotalPages()); // 전체 페이지 수
		model.addAttribute("searchKeyword", searchKeyword); // 검색 키워드
		model.addAttribute("size", size);

		return "admin/tag/tag_list";
	}

	/** AJAX/팝업용 — JSON 페이징 결과 반환 */
	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Page<TagInfoResponse> listTagsJson(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String searchKeyword) {

		return tagService.getTags(page, size, searchKeyword);
	}

	/** 태그 등록 또는 수정 폼 진입 */
	@GetMapping({"/new", "/{tagId}/edit"})
	public String tagForm(@PathVariable(value = "tagId", required = false) Long tagId,
		Model model,
		HttpServletRequest request) {
		log.info("어드민 태그 폼 페이지 요청. URI: {}, tagId: {}", request.getRequestURI(), tagId);
		model.addAttribute("currentURI", request.getRequestURI());

		String pageTitle;
		TagInfoResponse tag;

		if (tagId != null) {
			tag = tagService.getTag(tagId); // 수정 시 기존 데이터 조회
			pageTitle = "태그 수정 (ID: " + tagId + ")";
		} else {
			pageTitle = "새 태그 등록";
			tag = new TagInfoResponse(null, "", LocalDateTime.now()); // 새로운 데이터 생성
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("tag", tag);

		return "admin/tag/tag_form";
	}

	/** 태그 저장 */
	@PostMapping("/save")
	public String saveTag(@Valid @ModelAttribute("tag") TagInfoResponse tag,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {
		log.info("태그 저장 요청: {}", tag);

		// 기본 유효성 검사
		if (tag.getName() == null || tag.getName().trim().isEmpty()) {
			bindingResult.rejectValue("name", "NotEmpty", "태그 이름은 필수입니다.");
		}

		// 유효성 검사 실패 시 원래 폼으로 리다이렉트
		if (bindingResult.hasErrors()) {
			log.warn("태그 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.tag", bindingResult);
			redirectAttributes.addFlashAttribute("tag", tag);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");
			if (tag.getId() != null) {
				return "redirect:/admin/tags/" + tag.getId() + "/edit";
			} else {
				return "redirect:/admin/tags/new";
			}
		}

		try {
			// 실제 서비스 로직 호출 (DB에 저장/수정)
			if (tag.getId() == null) {
				tagService.createTag(new TagRequest(tag.getName()));
			} else {
				tagService.updateTag(tag.getId(), new TagRequest(tag.getName())); // name 만 수정
			}
			String action = (tag.getId() == null) ? "등록" : "수정";
			log.info("태그 {} 처리 (임시): Name={}, DeletedAt={}", action, tag.getName());
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"태그 '" + tag.getName() + "'가 성공적으로 " + action + "되었습니다.");
		} catch (Exception e) {
			log.error("태그 저장 중 오류 발생 ", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "태그 저장 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("tag", tag);
			if (tag.getId() != null) {
				return "redirect:/admin/tags/" + tag.getId() + "/edit";
			} else {
				return "redirect:/admin/tags/new";
			}
		}
		return "redirect:/admin/tags";
	}

	/** 태그 삭제 처리 */
	@PostMapping("/{tagId}/delete")
	public String deleteTag(@PathVariable Long tagId, RedirectAttributes redirectAttributes) {
		log.info("태그 삭제 요청: ID {}", tagId);
		try {
			// 실제 서비스 로직 호출 - tagId에 해당하는 레코드의 deleted_at을 현재 시간으로 업데이트
			tagService.deleteTag(tagId);
			log.info("태그 삭제 성공 처리: ID {}", tagId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "태그(ID: " + tagId + ")가 성공적으로 삭제되었습니다.");
		} catch (Exception e) {
			log.error("태그 비활성화 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "태그 비활성화 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/tags";
	}
}
