package shop.bluebooktle.frontend.controller.admin;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.dto.response.TagInfoResponse;

@Slf4j
@Controller
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {
	private final

	@Getter
	@Setter
	@ToString
	static class TagDto {
		private Long id; // tag_id
		private String name;
		private LocalDateTime createdAt;
		private LocalDateTime deletedAt;

		public TagDto() {
			// 기본 생성자
		}

		public TagDto(Long id, String name, LocalDateTime createdAt, LocalDateTime deletedAt) {
			this.id = id;
			this.name = name;
			this.createdAt = createdAt;
			this.deletedAt = deletedAt;
		}
	}

	/** 태그 목록 조회 */
	@GetMapping
	public String listTags(Model model, HttpServletRequest request,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword) {
		Pageable pageable = PageRequest.of(page - 1, size);
		Page<TagInfoResponse> tagPage = tagService.getTags(pageable);

		List<TagInfoResponse> filteredTags = tagPage.getContent().stream()
			.filter(t -> searchKeyword == null || t.getName().contains(searchKeyword))
			.collect(Collectors.toList());

		log.info("어드민 태그 목록 페이지 요청. URI: {}", request.getRequestURI());
		model.addAttribute("pageTitle", "태그 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		// TODO: 실제 태그 목록 조회
		List<TagDto> tags = Arrays.asList(
			new TagDto(1L, "스프링부트", LocalDateTime.now().minusDays(5), null),
			new TagDto(2L, "JPA", LocalDateTime.now().minusDays(3), null),
			new TagDto(3L, "소설", LocalDateTime.now().minusDays(10), null),
			new TagDto(4L, "자기계발", LocalDateTime.now().minusDays(12), LocalDateTime.now().minusDays(1)) // 비활성 예시
		);
		model.addAttribute("tags", tags);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", 1);
		model.addAttribute("searchKeyword", searchKeyword);

		return "admin/tag/tag_list";
	}

	@GetMapping({"/new", "/{tagId}/edit"})
	public String tagForm(@PathVariable(value = "tagId", required = false) Long tagId,
		Model model, HttpServletRequest request) {
		log.info("어드민 태그 폼 페이지 요청. URI: {}, tagId: {}", request.getRequestURI(), tagId);
		model.addAttribute("currentURI", request.getRequestURI());

		TagDto tagDto;
		String pageTitle;

		if (tagId != null) {
			pageTitle = "태그 수정 (ID: " + tagId + ")";
			// TODO: tagId로 실제 태그 정보 조회
			tagDto = new TagDto(tagId, "스프링부트 (수정)", LocalDateTime.now().minusDays(5), null);
		} else {
			pageTitle = "새 태그 등록";
			tagDto = new TagDto();
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("tag", tagDto);

		return "admin/tag/tag_form";
	}

	@PostMapping("/save")
	public String saveTag(@ModelAttribute("tag") TagDto tagDto,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("태그 저장 요청: {}", tagDto);

		if (tagDto.getName() == null || tagDto.getName().trim().isEmpty()) {
			bindingResult.rejectValue("name", "NotEmpty", "태그 이름은 필수입니다.");
		}
		// TODO: 태그 이름 중복 검사 등 추가 유효성 검사

		if (bindingResult.hasErrors()) {
			log.warn("태그 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.tag", bindingResult);
			redirectAttributes.addFlashAttribute("tag", tagDto);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");
			if (tagDto.getId() != null) {
				return "redirect:/admin/tags/" + tagDto.getId() + "/edit";
			} else {
				return "redirect:/admin/tags/new";
			}
		}

		try {
			// TODO: 실제 서비스 로직 호출 (DB에 저장/수정)
			// if (tagDto.getId() == null) { adminTagService.createTag(tagDto); } // deletedAt은 null로 저장
			// else { adminTagService.updateTag(tagDto); } // name만 수정, deletedAt은 유지

			String action = (tagDto.getId() == null) ? "등록" : "수정";
			log.info("태그 {} 처리 (임시): Name={}, DeletedAt={}", action, tagDto.getName(), tagDto.getDeletedAt());
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"태그 '" + tagDto.getName() + "'가 성공적으로 " + action + "되었습니다.");
		} catch (Exception e) {
			log.error("태그 저장 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "태그 저장 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("tag", tagDto);
			if (tagDto.getId() != null) {
				return "redirect:/admin/tags/" + tagDto.getId() + "/edit";
			} else {
				return "redirect:/admin/tags/new";
			}
		}
		return "redirect:/admin/tags";
	}

	@PostMapping("/{tagId}/delete")
	public String deleteTag(@PathVariable Long tagId, RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("태그 비활성화 요청: ID {}", tagId);
		try {
			// TODO: 실제 서비스 로직 호출 - tagId에 해당하는 레코드의 deleted_at을 현재 시간으로 업데이트
			// adminTagService.deactivateTag(tagId);
			log.info("임시 태그 비활성화 성공 처리: ID {}", tagId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "태그(ID: " + tagId + ")가 성공적으로 비활성화 처리되었습니다.");
		} catch (Exception e) {
			log.error("태그 비활성화 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "태그 비활성화 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/tags";
	}

	@PostMapping("/{tagId}/activate")
	public String activateTag(@PathVariable Long tagId, RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("태그 활성화 요청: ID {}", tagId);
		try {
			// TODO: 실제 서비스 로직 호출 - tagId에 해당하는 레코드의 deleted_at을 NULL로 업데이트
			// adminTagService.activateTag(tagId);
			log.info("임시 태그 활성화 성공 처리: ID {}", tagId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "태그(ID: " + tagId + ")가 성공적으로 활성화 처리되었습니다.");
		} catch (Exception e) {
			log.error("태그 활성화 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "태그 활성화 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/tags";
	}
}
