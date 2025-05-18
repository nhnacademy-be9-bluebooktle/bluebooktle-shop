package shop.bluebooktle.frontend.controller.admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/authors")
@RequiredArgsConstructor
public class AdminAuthorController {

	@Getter
	@Setter
	@ToString
	static class AuthorDto {
		private Long id;
		private String name;
		private String description;
		private String authorKey;
		private LocalDateTime createdAt;
		private LocalDateTime deletedAt;
		private boolean isActive;

		public AuthorDto() {
			this.isActive = true;
		}

		public AuthorDto(Long id, String name, String description, String authorKey, LocalDateTime createdAt,
			LocalDateTime deletedAt) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.authorKey = authorKey;
			this.createdAt = createdAt;
			this.deletedAt = deletedAt;
			this.isActive = (deletedAt == null);
		}
	}

	// 임시 작가 데이터 목록
	private static final List<AuthorDto> allAuthorsForDemo = new ArrayList<>();

	static {
		allAuthorsForDemo.add(
			new AuthorDto(1L, "김스프링", "스프링 부트 전문가. 다수의 베스트셀러 저자.", "KSPRING001", LocalDateTime.now().minusDays(10),
				null));
		allAuthorsForDemo.add(
			new AuthorDto(2L, "이자바", "자바 기초부터 고급까지, 명쾌한 설명으로 유명.", "LJAVA002", LocalDateTime.now().minusDays(5), null));
		allAuthorsForDemo.add(
			new AuthorDto(3L, "박노드", "Node.js와 풀스택 개발 가이드 저술.", "PNODE003", LocalDateTime.now().minusDays(20),
				LocalDateTime.now().minusDays(1)));
		for (int i = 4; i <= 25; i++) { // 데이터 추가
			allAuthorsForDemo.add(new AuthorDto((long)i, "작가 " + i, "작가 " + i + "에 대한 설명입니다. 다양한 분야에서 활동 중입니다.",
				"AUTHORKEY" + String.format("%03d", i), LocalDateTime.now().minusDays(i * 3),
				(i % 7 == 0) ? LocalDateTime.now().minusDays(i) : null));
		}
	}

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

		// 임시 필터링 로직
		List<AuthorDto> filteredAuthors = allAuthorsForDemo.stream()
			.filter(author -> (searchKeyword == null || searchKeyword.trim().isEmpty()) ||
				(searchField == null || searchField.trim().isEmpty()) || // 전체 검색 또는 필드 지정 검색
				(searchField.equals("name") && author.getName() != null && author.getName()
					.toLowerCase()
					.contains(searchKeyword.toLowerCase())) ||
				(searchField.equals("description") && author.getDescription() != null && author.getDescription()
					.toLowerCase()
					.contains(searchKeyword.toLowerCase())) ||
				(searchField.equals("authorKey") && author.getAuthorKey() != null && author.getAuthorKey()
					.toLowerCase()
					.contains(searchKeyword.toLowerCase()))
			)
			.collect(Collectors.toList());

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt"); // 기본 정렬
		Pageable pageable = PageRequest.of(page, size, sort);

		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), filteredAuthors.size());
		List<AuthorDto> pageContent =
			(start >= filteredAuthors.size() || start > end) ? List.of() : filteredAuthors.subList(start, end);
		Page<AuthorDto> authorsPage = new PageImpl<>(pageContent, pageable, filteredAuthors.size());

		log.info("Author Pagination Data: TotalElements={}, TotalPages={}, CurrentPage(0-based)={}, PageSize={}",
			authorsPage.getTotalElements(), authorsPage.getTotalPages(), authorsPage.getNumber(),
			authorsPage.getSize());

		model.addAttribute("authors", authorsPage.getContent());
		model.addAttribute("currentPage", authorsPage.getNumber()); // 0-based 현재 페이지
		model.addAttribute("totalPages", authorsPage.getTotalPages());     // 전체 페이지 수
		model.addAttribute("currentSize", authorsPage.getSize());         // 현재 페이지 크기 (뷰에서 사용)
		model.addAttribute("totalElements", authorsPage.getTotalElements()); // 필터링된 전체 아이템 수

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(request.getRequestURI())
			.queryParam("size", authorsPage.getSize());
		if (searchField != null && !searchField.isEmpty())
			uriBuilder.queryParam("searchField", searchField); // 검색 필드 추가
		if (searchKeyword != null && !searchKeyword.isEmpty())
			uriBuilder.queryParam("searchKeyword", searchKeyword);

		String baseUrlWithParams = uriBuilder.toUriString();
		log.info("Base URL for author pagination (excluding 'page' param): {}", baseUrlWithParams);
		model.addAttribute("baseUrlWithParams", baseUrlWithParams);

		model.addAttribute("searchField", searchField); // 검색 필드 유지
		model.addAttribute("searchKeyword", searchKeyword); // 검색어 유지
		model.addAttribute("allAuthorsForDemo", allAuthorsForDemo); // 총 건수 표시용 임시 데이터

		return "admin/author/author_list";
	}

	// authorForm, saveAuthor, deleteAuthor 메소드는 기존 내용 유지 (필요시 DTO 필드 등 확인)
	@GetMapping({"/new", "/{authorId}/edit"})
	public String authorForm(@PathVariable(value = "authorId", required = false) Long authorId,
		Model model, HttpServletRequest request) {
		log.info("어드민 작가 폼 페이지 요청. URI: {}, authorId: {}", request.getRequestURI(), authorId);
		model.addAttribute("currentURI", request.getRequestURI());

		AuthorDto authorDto;
		String pageTitle;

		if (authorId != null) {
			pageTitle = "작가 정보 수정 (ID: " + authorId + ")";
			final Long finalAuthorId = authorId;
			authorDto = allAuthorsForDemo.stream()
				.filter(a -> a.getId().equals(finalAuthorId))
				.findFirst()
				.orElse(new AuthorDto()); // 못 찾으면 빈 객체
			if (authorDto.getId() == null && authorId != null) { // ID는 있는데 데모 데이터에 없는 경우
				authorDto.setId(authorId); // ID라도 설정
				log.warn("Author with ID {} not found in demo data for edit form.", authorId);
			}
		} else {
			pageTitle = "새 작가 등록";
			authorDto = new AuthorDto();
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("author", authorDto);

		return "admin/author/author_form";
	}

	@PostMapping("/save")
	public String saveAuthor(@ModelAttribute("author") AuthorDto authorDto,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("작가 저장 요청: {}", authorDto);

		if (authorDto.getName() == null || authorDto.getName().trim().isEmpty()) {
			bindingResult.rejectValue("name", "NotEmpty", "작가 이름은 필수입니다.");
		}
		if (authorDto.getDescription() == null || authorDto.getDescription().trim().isEmpty()) {
			bindingResult.rejectValue("description", "NotEmpty", "작가 설명은 필수입니다.");
		}
		if (authorDto.getAuthorKey() == null || authorDto.getAuthorKey().trim().isEmpty()) { // authorKey 필드 추가
			bindingResult.rejectValue("authorKey", "NotEmpty", "작가 키는 필수입니다.");
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
			// TODO: 실제 서비스 로직
			String action = (authorDto.getId() == null) ? "등록" : "수정";
			log.info("작가 {} 처리 (임시): {}", action, authorDto.getName());
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
			// TODO: 실제 삭제 로직
			log.info("임시 작가 삭제 성공 처리: ID {}", authorId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"작가(ID: " + authorId + ")가 성공적으로 삭제(비활성)되었습니다.");
		} catch (Exception e) {
			log.error("작가 삭제 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "작가 삭제 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/authors";
	}
}