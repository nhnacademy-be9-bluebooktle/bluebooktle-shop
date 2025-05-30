package shop.bluebooktle.frontend.controller.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookFormRequest;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.frontend.service.AdminAuthorService;
import shop.bluebooktle.frontend.service.AdminBookService;
import shop.bluebooktle.frontend.service.AdminCategoryService;
import shop.bluebooktle.frontend.service.AdminImgService;
import shop.bluebooktle.frontend.service.AdminPublisherService;
import shop.bluebooktle.frontend.service.AdminTagService;

// AdminCategoryController.CategoryDto를 사용하기 위해 import (같은 패키지면 필요 없을 수 있음)
// import shop.bluebooktle.frontend.controller.admin.AdminCategoryController.CategoryDto;
// AdminAuthorController.AuthorDto, AdminPublisherController.PublisherDto도 마찬가지

@Slf4j
@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

	private final AdminImgService imgService;
	private final AdminPublisherService adminPublisherService;
	private final AdminAuthorService adminAuthorService;
	private final AdminTagService adminTagService;
	private final AdminBookService adminBookService;
	private final AdminCategoryService adminCategoryService;

	@GetMapping
	public String listBooks(
		Model model,
		HttpServletRequest request,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	) {
		log.info("AdminBookController - listBooks: page={}, size={}, searchKeyword={}",
			page, size, searchKeyword);

		var booksPage = adminBookService.getPagedBooks(page, size, searchKeyword);

		model.addAttribute("pageTitle", "도서 관리");
		model.addAttribute("currentURI", request.getRequestURI());
		model.addAttribute("books", booksPage.getContent());
		model.addAttribute("currentPageZeroBased", booksPage.getNumber());
		model.addAttribute("totalPages", booksPage.getTotalPages());
		model.addAttribute("currentSize", booksPage.getSize());
		model.addAttribute("totalElements", booksPage.getTotalElements());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromPath(request.getRequestURI())
			.queryParam("size", size);
		if (StringUtils.hasText(searchKeyword))
			uriBuilder.queryParam("searchKeyword", searchKeyword);
		model.addAttribute("baseUrlWithParams", uriBuilder.toUriString());

		model.addAttribute("stateOptions", Arrays.asList(
			BookSaleInfoState.AVAILABLE.name(),
			BookSaleInfoState.LOW_STOCK.name(),
			BookSaleInfoState.SALE_ENDED.name(),
			BookSaleInfoState.DELETED.name()
		));
		model.addAttribute("allCategoriesForMapping",
			adminCategoryService.getCategories(0, Integer.MAX_VALUE, null).getContent());

		model.addAttribute("searchKeyword", searchKeyword);

		return "admin/book/book_list";
	}

	@GetMapping({"/new"})
	public String bookForm(
		@PathVariable(value = "bookId", required = false) Long bookId,
		HttpServletRequest request,
		Model model) {
		log.info("어드민 도서 폼 페이지 요청. URI: {}, bookId: {}", request.getRequestURI(), bookId);
		model.addAttribute("currentURI", request.getRequestURI());

		// 카테고리, 작가, 출판사, 태그

		List<AuthorResponse> allAuthorsForMapping =
			adminAuthorService.getAuthors(0, Integer.MAX_VALUE, null)
				.getContent();

		List<CategoryTreeResponse> allCategoriesForMapping =
			adminCategoryService.getCategoryTree();

		List<PublisherInfoResponse> allPublishersForMapping =
			adminPublisherService.getPublishers(0, Integer.MAX_VALUE, null)
				.getContent();

		List<TagInfoResponse> allTagsForMapping =
			adminTagService.getTags(0, Integer.MAX_VALUE, null)
				.getContent();

		String pageTitle;

		pageTitle = "새 도서 등록";

		model.addAttribute("pageTitle", pageTitle);

		if (!model.containsAttribute("bookForm")) {
			model.addAttribute("bookForm", new BookFormRequest("",
				"",
				"",
				"",
				LocalDate.now(),
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				0,
				false,
				BookSaleInfoState.AVAILABLE,
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				null));
		}
		model.addAttribute("allCategoriesForMapping", allCategoriesForMapping);
		model.addAttribute("allAuthorsForMapping", allAuthorsForMapping);
		model.addAttribute("allPublishers", allPublishersForMapping);
		model.addAttribute("allTagsForMapping", allTagsForMapping);

		model.addAttribute("stateOptions", Arrays.asList(
			BookSaleInfoState.AVAILABLE.name(),
			BookSaleInfoState.LOW_STOCK.name(),
			BookSaleInfoState.SALE_ENDED.name(),
			BookSaleInfoState.DELETED.name()
		));

		return "admin/book/book_form";
	}

	@GetMapping({"/{bookId}/edit"})
	public String bookEditForm(
		@PathVariable(value = "bookId", required = false) Long bookId,
		HttpServletRequest request,
		Model model) {
		log.info("어드민 도서 폼 페이지 요청. URI: {}, bookId: {}", request.getRequestURI(), bookId);
		model.addAttribute("currentURI", request.getRequestURI());

		List<AuthorResponse> allAuthorsForMapping =
			adminAuthorService.getAuthors(0, Integer.MAX_VALUE, null)
				.getContent();

		List<CategoryTreeResponse> allCategoriesForMapping =
			adminCategoryService.getCategoryTree();

		List<PublisherInfoResponse> allPublishersForMapping =
			adminPublisherService.getPublishers(0, Integer.MAX_VALUE, null)
				.getContent();

		List<TagInfoResponse> allTagsForMapping =
			adminTagService.getTags(0, Integer.MAX_VALUE, null)
				.getContent();

		BookAllRegisterRequest formDto;
		String pageTitle;
		if (bookId != null) {
			pageTitle = "도서 정보 수정 (ID: " + bookId + ")";
			BookAllResponse resp = adminBookService.getBook(bookId);

			List<Long> authorIds = allAuthorsForMapping.stream()
				.filter(a -> resp.getAuthors().contains(a.getName()))
				.map(AuthorResponse::getId)
				.toList();

			List<Long> categoryIds = allCategoriesForMapping.stream()
				.filter(c -> resp.getCategories().contains(c.name()))
				.map(CategoryTreeResponse::id)
				.toList();

			List<Long> publisherIds = allPublishersForMapping.stream()
				.filter(p -> resp.getPublishers().contains(p.getName()))
				.map(PublisherInfoResponse::getId)
				.toList();

			List<Long> tagIds = allTagsForMapping.stream()
				.filter(t -> resp.getTags().contains(t.getName()))
				.map(TagInfoResponse::getId)
				.toList();

			formDto = BookAllRegisterRequest.builder()
				.title(resp.getTitle())
				.isbn(resp.getIsbn())
				.index(resp.getIndex())
				.description(resp.getDescription())
				.publishDate(resp.getPublishDate().toLocalDate())
				.price(resp.getPrice())
				.salePrice(resp.getSalePrice())
				.stock(resp.getStock())
				.isPackable(resp.getIsPackable())
				.state(resp.getBookSaleInfoState())
				.authorIdList(authorIds)
				.publisherIdList(publisherIds)
				.categoryIdList(categoryIds)
				.tagIdList(tagIds)
				.imgUrl(resp.getImgUrl())
				.build();
		} else {
			pageTitle = "새 도서 등록";
			formDto = BookAllRegisterRequest.builder()
				.title("")
				.isbn("")
				.index("")
				.description("")
				.publishDate(LocalDate.now())
				.price(BigDecimal.ZERO)
				.salePrice(BigDecimal.ZERO)
				.stock(0)
				.isPackable(false)
				.state(BookSaleInfoState.AVAILABLE)
				.authorIdList(Collections.emptyList())
				.publisherIdList(Collections.emptyList())
				.categoryIdList(Collections.emptyList())
				.tagIdList(Collections.emptyList())
				.imgUrl("")
				.build();
		}

		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("book", formDto);
		model.addAttribute("allCategoriesForMapping", allCategoriesForMapping);
		model.addAttribute("allAuthorsForMapping", allAuthorsForMapping);
		model.addAttribute("allPublishers", allPublishersForMapping);
		model.addAttribute("allTagsForMapping", allTagsForMapping);

		model.addAttribute("stateOptions", Arrays.asList(
			BookSaleInfoState.AVAILABLE.name(),
			BookSaleInfoState.LOW_STOCK.name(),
			BookSaleInfoState.SALE_ENDED.name(),
			BookSaleInfoState.DELETED.name()
		));

		model.addAttribute("presignedUploadUrl", imgService.getPresignedUploadUrl());

		return "admin/book/book_form";
	}

	@PostMapping("/save")
	public String saveBook(
		@Valid @ModelAttribute BookFormRequest bookFormRequest,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request,
		Model model
	) {
		log.info(bookFormRequest.toString());

		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.bookFormRequest",
				bindingResult);
			redirectAttributes.addFlashAttribute("bookForm", bookFormRequest);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");
			log.info("에러 로그");
			return "redirect:/admin/books/new";
		}
		try {
			adminBookService.registerBook(bookFormRequest);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "책 등록 중 오류 발생: " + e.getMessage());
			redirectAttributes.addAttribute("bookForm", bookFormRequest);
			return "redirect:/admin/books/new";

		}

		redirectAttributes.addFlashAttribute("globalSuccessMessage",
			"도서 '" + bookFormRequest.getTitle() + "' 이(가) 성공적으로 등록되었습니다.");

		return "redirect:/admin/books";
	}

	@PostMapping("/{bookId}/delete")
	public String deleteBook(@PathVariable Long bookId, RedirectAttributes redirectAttributes) {
		log.info("도서 비활성화(삭제) 요청: ID {}", bookId);
		adminBookService.deleteBook(bookId);
		redirectAttributes.addFlashAttribute("globalSuccessMessage", "도서(ID:" + bookId + ")가 비활성화되었습니다.");
		return "redirect:/admin/books";
	}

	@GetMapping("/aladin-search")
	@ResponseBody
	public List<AladinBookResponse> aladinSearch(
		@RequestParam("keyword") String keyword,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	) {
		return adminBookService.searchAladin(keyword, page, size);
	}

}
