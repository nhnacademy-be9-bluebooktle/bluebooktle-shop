package shop.bluebooktle.frontend.controller.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.common.dto.book.request.BookFormRequest;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.frontend.service.AdminAuthorService;
import shop.bluebooktle.frontend.service.AdminBookService;
import shop.bluebooktle.frontend.service.AdminCategoryService;
import shop.bluebooktle.frontend.service.AdminImgService;
import shop.bluebooktle.frontend.service.AdminPublisherService;
import shop.bluebooktle.frontend.service.AdminTagService;

@Slf4j
@Controller
@RequestMapping("/admin/aladin/books")
@RequiredArgsConstructor
public class AdminAladinBookController {

	private final AdminImgService adminImgService;
	private final AdminPublisherService adminPublisherService;
	private final AdminAuthorService adminAuthorService;
	private final AdminTagService adminTagService;
	private final AdminBookService adminBookService;
	private final AdminCategoryService adminCategoryService;
	private Model model;

	@GetMapping({"/new"})
	public String bookForm(
		@PathVariable(value = "bookId", required = false) Long bookId,
		HttpServletRequest request,
		Model model) {
		log.info("어드민 도서 폼 페이지 요청. URI: {}, bookId: {}", request.getRequestURI(), bookId);
		model.addAttribute("currentURI", request.getRequestURI());

		// 카테고리, 태그
		List<CategoryTreeResponse> allCategoriesForMapping =
			adminCategoryService.getCategoryTree();

		List<TagInfoResponse> allTagsForMapping =
			adminTagService.getTags(0, Integer.MAX_VALUE, null)
				.getContent();

		String pageTitle;

		pageTitle = "알라딘 API 도서 등록";
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
		model.addAttribute("allTagsForMapping", allTagsForMapping);

		model.addAttribute("stateOptions", Arrays.asList(
			BookSaleInfoState.AVAILABLE.name(),
			BookSaleInfoState.LOW_STOCK.name(),
			BookSaleInfoState.SALE_ENDED.name(),
			BookSaleInfoState.DELETED.name()
		));

		return "admin/aladin_book/aladin_book_form";
	}

	@PostMapping("/save")
	public String saveBook(
		@Valid @ModelAttribute BookAllRegisterByAladinRequest aladinBookFormRequest,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request,
		Model model
	) {
		if (bindingResult.hasErrors()) {
			log.warn("도서 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.bookFormRequest",
				bindingResult);
			redirectAttributes.addFlashAttribute("bookForm", aladinBookFormRequest);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");
			return "redirect:/admin/aladin/books/new";
		}
		try {
			adminBookService.registerBookByAladin(aladinBookFormRequest);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "알라딘 API 도서 등록 중 오류 발생: " + e.getMessage());
			return "redirect:/admin/aladin/books/new";
		}
		redirectAttributes.addFlashAttribute("globalSuccessMessage",
			"알라딘 도서 isbn : '" + aladinBookFormRequest.getIsbn() + "' 이(가) 성공적으로 등록되었습니다.");
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
