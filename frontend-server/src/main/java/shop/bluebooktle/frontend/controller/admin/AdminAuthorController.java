package shop.bluebooktle.frontend.controller.admin;

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
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.request.author.AuthorRegisterRequest;
import shop.bluebooktle.common.dto.book.request.author.AuthorRequest;
import shop.bluebooktle.common.dto.book.request.author.AuthorUpdateRequest;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.frontend.service.AdminAuthorService;

@Slf4j
@Controller
@RequestMapping("/admin/authors")
@RequiredArgsConstructor
public class AdminAuthorController {

	private final AdminAuthorService adminAuthorService;

	@GetMapping
	public String listAuthors(Model model, HttpServletRequest request,
		@RequestParam(value = "page", defaultValue = "0") int page, // 0-based
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchField", required = false) String searchField, // 검색 필드 추가
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword) {
		log.info("AdminAuthorController - listAuthors: page={}, size={}, searchField={}, searchKeyword={}", page, size,
			searchField, searchKeyword);
		model.addAttribute("pageTitle", "작가 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		Page<AuthorResponse> authorsPage = adminAuthorService.getAuthors(page, size, searchKeyword);

		log.info("Author Pagination Data: TotalElements={}, TotalPages={}, CurrentPage(0-based)={}, PageSize={}",
			authorsPage.getTotalElements(), authorsPage.getTotalPages(), authorsPage.getNumber(),
			authorsPage.getSize());

		model.addAttribute("authors", authorsPage.getContent());
		model.addAttribute("currentPage", authorsPage.getNumber());
		model.addAttribute("totalPages", authorsPage.getTotalPages());
		model.addAttribute("currentSize", authorsPage.getSize());
		model.addAttribute("totalElements", authorsPage.getTotalElements());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(request.getRequestURI())
			.queryParam("size", authorsPage.getSize());
		if (searchField != null && !searchField.isEmpty())
			uriBuilder.queryParam("searchField", searchField);
		if (searchKeyword != null && !searchKeyword.isEmpty())
			uriBuilder.queryParam("searchKeyword", searchKeyword);

		String baseUrlWithParams = uriBuilder.toUriString();
		log.info("Base URL for author pagination (excluding 'page' param): {}", baseUrlWithParams);
		model.addAttribute("baseUrlWithParams", baseUrlWithParams);

		model.addAttribute("searchField", searchField);
		model.addAttribute("searchKeyword", searchKeyword);

		return "admin/author/author_list";
	}

	/** AJAX/팝업용 — JSON 페이징 결과 반환 */
	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Page<AuthorResponse> listAuthorsJson(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String searchKeyword
	) {
		return adminAuthorService.getAuthors(page, size, searchKeyword);
	}

	// authorForm, saveAuthor, deleteAuthor 메소드는 기존 내용 유지 (필요시 DTO 필드 등 확인)
	@GetMapping({"/new", "/{authorId}/edit"})
	public String authorForm(@PathVariable(value = "authorId", required = false) Long authorId,
		Model model, HttpServletRequest request) {
		log.info("어드민 작가 폼 페이지 요청. URI: {}, authorId: {}", request.getRequestURI(), authorId);
		model.addAttribute("currentURI", request.getRequestURI());
		AuthorResponse authorDto;
		String pageTitle;

		if (authorId != null) {
			pageTitle = "작가 정보 수정 (ID: " + authorId + ")";
			authorDto = AuthorResponse.builder().id(authorId).build();
			log.warn("Author with ID {} not found in demo data for edit form.", authorId);
		} else {
			pageTitle = "새 작가 등록";
			authorDto = AuthorResponse.builder().build();
		}

		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("author", authorDto);

		return "admin/author/author_form";
	}

	@PostMapping("/save")
	public String saveAuthor(@ModelAttribute("author") AuthorRequest authorDto,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("작가 저장 요청: {}", authorDto);

		if (authorDto.getName() == null || authorDto.getName().trim().isEmpty()) {
			bindingResult.rejectValue("name", "NotEmpty", "작가 이름은 필수입니다.");
		}

		if (bindingResult.hasErrors()) {
			log.warn("작가 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			// 오류 발생 시, 폼으로 다시 돌아갈 때 입력값을 유지하기 위해 FlashAttribute 사용
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.author", bindingResult);
			redirectAttributes.addFlashAttribute("author", authorDto); // 입력한 DTO 다시 전달
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");

			if (authorDto.getId() != null) {
				return "redirect:/admin/authors/" + authorDto.getId() + "/edit";
			} else {
				return "redirect:/admin/authors/new";
			}
		}
		try {
			String action = (authorDto.getId() == null) ? "등록" : "수정";
			log.info("작가 {} 처리 (임시): {}", action, authorDto.getName());
			if (authorDto.getId() != null) {
				adminAuthorService.updateAuthor(authorDto.getId(),
					AuthorUpdateRequest.builder().name(authorDto.getName()).build());
			} else {
				adminAuthorService.addAuthor(AuthorRegisterRequest.builder().name(authorDto.getName()).build());
			}
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"작가 '" + authorDto.getName() + "' 정보가 성공적으로 " + action + "되었습니다.");
		} catch (Exception e) {
			log.error("작가 저장 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "작가 저장 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("author", authorDto);
			if (authorDto.getId() != null) {
				return "redirect:/admin/authors/" + authorDto.getId() + "/edit";
			} else {
				return "redirect:/admin/authors/new";
			}
		}
		return "redirect:/admin/authors";
	}

	@PostMapping("/{authorId}/delete")
	public String deleteAuthor(@PathVariable Long authorId, RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("작가 삭제 요청: ID {}", authorId);
		try {
			log.info("임시 작가 삭제 성공 처리: ID {}", authorId);
			adminAuthorService.deleteAuthor(authorId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"작가(ID: " + authorId + ")가 성공적으로 삭제(비활성)되었습니다.");
		} catch (Exception e) {
			log.error("작가 삭제 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "작가 삭제 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/authors";
	}
}