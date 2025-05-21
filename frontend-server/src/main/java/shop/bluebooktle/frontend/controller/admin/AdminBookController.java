package shop.bluebooktle.frontend.controller.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

// AdminCategoryController.CategoryDto를 사용하기 위해 import (같은 패키지면 필요 없을 수 있음)
// import shop.bluebooktle.frontend.controller.admin.AdminCategoryController.CategoryDto;
// AdminAuthorController.AuthorDto, AdminPublisherController.PublisherDto도 마찬가지

@Slf4j
@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

	// --- DTO 정의 (BookListDto, BookFormDto는 기존 내용 유지) ---
	@Getter
	@Setter
	@ToString
	static class BookListDto {
		private Long id;
		private String title;
		private String isbn;
		private String mainAuthorName;
		private String mainPublisherName;
		private BigDecimal salePrice;
		private String saleState;
		private Integer stock;
		private LocalDateTime createdAt;
		private LocalDateTime deletedAt;

		public BookListDto(Long id, String title, String isbn, String mainAuthorName, String mainPublisherName,
			BigDecimal salePrice, String saleState, Integer stock, LocalDateTime createdAt, LocalDateTime deletedAt) {
			this.id = id;
			this.title = title;
			this.isbn = isbn;
			this.mainAuthorName = mainAuthorName;
			this.mainPublisherName = mainPublisherName;
			this.salePrice = salePrice;
			this.saleState = saleState;
			this.stock = stock;
			this.createdAt = createdAt;
			this.deletedAt = deletedAt;
		}
	}

	@Getter
	@Setter
	@ToString
	static class BookFormDto {
		private Long id;
		private String title;
		private String isbn;
		private String bookIndex;
		private String description;
		private LocalDate publishDate;
		private LocalDateTime deletedAt;
		private BigDecimal regularPrice;
		private BigDecimal salePrice;
		private Integer stock;
		private Boolean isPackable;
		private String saleState;
		private List<Long> categoryIds = new ArrayList<>();
		private List<Long> authorIds = new ArrayList<>();
		private Long publisherId;
		private String newAuthorName;
		private String newPublisherName;
		private String tagsInput;

		public BookFormDto() {
			this.isPackable = false;
			this.saleState = "AVAILABLE";
		}
	}

	// 임시 도서 데이터 목록
	private static final List<BookListDto> allBooksForDemo = new ArrayList<>();

	static {
		allBooksForDemo.add(
			new BookListDto(1L, "스프링 부트 실전", "9791162243302", "김영한", "위키북스", new BigDecimal("30000"), "AVAILABLE", 100,
				LocalDateTime.now().minusDays(10), null));
		allBooksForDemo.add(
			new BookListDto(2L, "JPA 프로그래밍 입문", "9788968480473", "박재성", "에이콘", new BigDecimal("35000"), "LOW_STOCK", 10,
				LocalDateTime.now().minusDays(5), null));
		allBooksForDemo.add(
			new BookListDto(3L, "모던 자바스크립트", "9791162241469", "이웅모", "위키북스", new BigDecimal("42000"), "SALE_ENDED", 0,
				LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(1)));
		for (int i = 4; i <= 30; i++) { // 데이터 수 증가
			allBooksForDemo.add(
				new BookListDto((long)i, "테스트 도서 " + i, "123456789012" + i, "저자 " + (i % 5 + 1), "출판사 " + (i % 3 + 1),
					new BigDecimal(20000 + (i * 1000)),
					(i % 4 == 0) ? "SALE_ENDED" : (i % 4 == 1) ? "LOW_STOCK" : "AVAILABLE", i * 5,
					LocalDateTime.now().minusDays(i * 2), (i % 10 == 0) ? LocalDateTime.now().minusDays(i) : null));
		}
	}

	@GetMapping
	public String listBooks(Model model, HttpServletRequest request,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchField", required = false) String searchField,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
		@RequestParam(value = "categoryFilter", required = false) Long categoryFilter,
		@RequestParam(value = "statusFilter", required = false) String statusFilter) {

		log.info(
			"AdminBookController - listBooks: page={}, size={}, searchField={}, searchKeyword={}, categoryFilter={}, statusFilter={}",
			page, size, searchField, searchKeyword, categoryFilter, statusFilter);
		model.addAttribute("pageTitle", "도서 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		List<BookListDto> filteredBooks = allBooksForDemo.stream()
			.filter(b -> (searchKeyword == null || searchKeyword.trim().isEmpty()) ||
				(searchField == null || searchField.trim().isEmpty()) ||
				(searchField.equals("title") && b.getTitle() != null && b.getTitle()
					.toLowerCase()
					.contains(searchKeyword.toLowerCase())) ||
				(searchField.equals("isbn") && b.getIsbn() != null && b.getIsbn().contains(searchKeyword)) ||
				(searchField.equals("author") && b.getMainAuthorName() != null && b.getMainAuthorName()
					.toLowerCase()
					.contains(searchKeyword.toLowerCase())) ||
				(searchField.equals("publisher") && b.getMainPublisherName() != null && b.getMainPublisherName()
					.toLowerCase()
					.contains(searchKeyword.toLowerCase())))
			.filter(
				b -> (statusFilter == null || statusFilter.isEmpty()) || (b.getSaleState() != null && b.getSaleState()
					.equals(statusFilter)))
			.collect(Collectors.toList());

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		Pageable pageable = PageRequest.of(page, size, sort);

		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), filteredBooks.size());
		List<BookListDto> pageContent =
			(start >= filteredBooks.size() || start > end) ? List.of() : filteredBooks.subList(start, end);
		Page<BookListDto> booksPage = new PageImpl<>(pageContent, pageable, filteredBooks.size());

		log.info("Book Pagination Data: TotalElements={}, TotalPages={}, CurrentPage(0-based)={}, PageSize={}",
			booksPage.getTotalElements(), booksPage.getTotalPages(), booksPage.getNumber(), booksPage.getSize());

		model.addAttribute("books", booksPage.getContent());
		model.addAttribute("currentPageZeroBased", booksPage.getNumber()); // 변수명 수정됨
		model.addAttribute("totalPages", booksPage.getTotalPages());
		model.addAttribute("currentSize", booksPage.getSize());
		model.addAttribute("totalElements", booksPage.getTotalElements());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(request.getRequestURI())
			.queryParam("size", booksPage.getSize());
		if (searchField != null && !searchField.isEmpty())
			uriBuilder.queryParam("searchField", searchField);
		if (searchKeyword != null && !searchKeyword.isEmpty())
			uriBuilder.queryParam("searchKeyword", searchKeyword);
		if (categoryFilter != null)
			uriBuilder.queryParam("categoryFilter", categoryFilter);
		if (statusFilter != null && !statusFilter.isEmpty())
			uriBuilder.queryParam("statusFilter", statusFilter);

		String baseUrlWithParams = uriBuilder.toUriString();
		log.info("Base URL for book pagination (excluding 'page' param): {}", baseUrlWithParams);
		model.addAttribute("baseUrlWithParams", baseUrlWithParams);

		model.addAttribute("searchField", searchField);
		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("categoryFilter", categoryFilter);
		model.addAttribute("statusFilter", statusFilter);
		model.addAttribute("allBooksForDemo", allBooksForDemo);

		model.addAttribute("saleStateOptions", Arrays.asList("AVAILABLE", "LOW_STOCK", "SALE_ENDED", "DELETED"));

		return "admin/book/book_list";
	}

	@GetMapping({"/new", "/{bookId}/edit"})
	public String bookForm(@PathVariable(value = "bookId", required = false) Long bookId,
		Model model, HttpServletRequest request) {
		log.info("어드민 도서 폼 페이지 요청. URI: {}, bookId: {}", request.getRequestURI(), bookId);
		model.addAttribute("currentURI", request.getRequestURI());

		BookFormDto bookFormDto;
		String pageTitle;

		if (bookId != null) {
			pageTitle = "도서 정보 수정 (ID: " + bookId + ")";
			final Long finalBookId = bookId;
			Optional<BookListDto> bookOpt = allBooksForDemo.stream() // BookListDto에서 찾음
				.filter(b -> b.getId().equals(finalBookId))
				.findFirst();

			if (bookOpt.isPresent()) {
				BookListDto b = bookOpt.get();
				bookFormDto = new BookFormDto(); // BookFormDto로 변환
				bookFormDto.setId(b.getId());
				bookFormDto.setTitle(b.getTitle());
				bookFormDto.setIsbn(b.getIsbn());
				bookFormDto.setSalePrice(b.getSalePrice());
				bookFormDto.setStock(b.getStock());
				bookFormDto.setSaleState(b.getSaleState());
				bookFormDto.setDeletedAt(b.getDeletedAt());
				// BookFormDto에만 있는 필드들은 임시값 또는 추가 조회 필요
				bookFormDto.setPublishDate(LocalDate.now().minusYears(1)); // 임시
				bookFormDto.setDescription("임시 설명 for " + b.getTitle());
				bookFormDto.setBookIndex("임시 목차");
				bookFormDto.setRegularPrice(b.getSalePrice() != null ? b.getSalePrice().add(new BigDecimal("3000")) :
					new BigDecimal("33000")); // null 체크 추가
				bookFormDto.setIsPackable(true); // 예시
				// categoryIds, authorIds, publisherId, tagsInput 등은 별도 조회 및 설정 필요
			} else {
				bookFormDto = new BookFormDto();
				bookFormDto.setId(bookId); // ID만 설정
				log.warn("Book with ID {} not found in demo data for edit form.", bookId);
			}
		} else {
			pageTitle = "새 도서 등록";
			bookFormDto = new BookFormDto();
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("book", bookFormDto);

		// AdminCategoryController.CategoryDto 생성자 시그니처에 맞게 수정
		// 생성자: CategoryDto(Long id, String name, Long parentId, String categoryPath, LocalDateTime createdAt, LocalDateTime deletedAt)
		model.addAttribute("allCategories", Arrays.asList(
			new AdminCategoryController.CategoryDto(1L, "국내도서", null, "/1", LocalDateTime.now().minusDays(10), null),
			new AdminCategoryController.CategoryDto(2L, "소설", 1L, "/1/2", LocalDateTime.now().minusDays(9), null),
			new AdminCategoryController.CategoryDto(3L, "해외도서", null, "/1", LocalDateTime.now().minusDays(8), null)
			// depth, parentName, isActive는 CategoryDto 내부 로직 또는 flattenCategories에서 처리됨 (AdminCategoryController 참고)
		));

		// AdminAuthorController.AuthorDto 생성자 시그니처 확인 필요 (AdminAuthorController의 DTO 정의에 따름)
		// 예시: public AuthorDto(Long id, String name, String description, String authorKey, LocalDateTime createdAt, LocalDateTime deletedAt)
		model.addAttribute("allAuthors", Arrays.asList(
			new AdminAuthorController.AuthorDto(1L, "김영한", "스프링 전문가", "AUTHOR_KIM", LocalDateTime.now().minusMonths(1),
				null)
		));

		// AdminPublisherController.PublisherDto 생성자 시그니처 확인 필요
		// 예시: public PublisherDto(Long id, String name, LocalDateTime createdAt, LocalDateTime deletedAt)
		model.addAttribute("allPublishers", Arrays.asList(
			new AdminPublisherController.PublisherDto(1L, "위키북스", LocalDateTime.now().minusMonths(2), null)
		));
		model.addAttribute("saleStateOptions", Arrays.asList("AVAILABLE", "LOW_STOCK", "SALE_ENDED", "DELETED"));

		return "admin/book/book_form";
	}

	@PostMapping("/save")
	public String saveBook(@ModelAttribute("book") BookFormDto bookFormDto,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("도서 저장 요청: {}", bookFormDto);

		if (bookFormDto.getTitle() == null || bookFormDto.getTitle().trim().isEmpty()) {
			bindingResult.rejectValue("title", "NotEmpty", "도서 제목은 필수입니다.");
		}

		if (bindingResult.hasErrors()) {
			log.warn("도서 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.book", bindingResult);
			redirectAttributes.addFlashAttribute("book", bookFormDto);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");
			if (bookFormDto.getId() != null) {
				return "redirect:/admin/books/" + bookFormDto.getId() + "/edit";
			} else {
				return "redirect:/admin/books/new";
			}
		}

		try {
			String action = (bookFormDto.getId() == null) ? "등록" : "수정";
			log.info("도서 {} 처리 (임시): {}", action, bookFormDto.getTitle());
			// TODO: 실제 저장 로직
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"도서 '" + bookFormDto.getTitle() + "' 정보가 성공적으로 " + action + "되었습니다.");
		} catch (Exception e) {
			log.error("도서 저장 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "도서 저장 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("book", bookFormDto);
			if (bookFormDto.getId() != null) {
				return "redirect:/admin/books/" + bookFormDto.getId() + "/edit";
			} else {
				return "redirect:/admin/books/new";
			}
		}
		return "redirect:/admin/books";
	}

	@PostMapping("/{bookId}/delete")
	public String deleteBook(@PathVariable Long bookId, RedirectAttributes redirectAttributes) {
		log.info("도서 비활성화(삭제) 요청: ID {}", bookId);
		// TODO: 실제 삭제 로직
		redirectAttributes.addFlashAttribute("globalSuccessMessage", "도서(ID:" + bookId + ")가 비활성화되었습니다.");
		return "redirect:/admin/books";
	}
}