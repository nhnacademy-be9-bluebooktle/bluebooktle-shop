package shop.bluebooktle.frontend.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.frontend.service.BookService;
import shop.bluebooktle.frontend.service.CategoryService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

	private final BookService bookService;
	private final CategoryService categoryService;

	@GetMapping("/")
	public String mainPage(
		Model model,
		HttpServletRequest request,
		HttpServletResponse response,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "20") int size
	) {
		Long hotCategoryId = categoryService.getCategoryByName("베스트셀러").categoryId();

		// HOT 도서: 카테고리 기반
		Page<BookInfoResponse> hotBooks = bookService.getPagedBooksByCategoryId(page, size, BookSortType.NEWEST,
			hotCategoryId);
		CategoryResponse hotCategory = bookService.getCategoryById(hotCategoryId);

		// NEW 도서: 전체 최신 도서
		Page<BookInfoResponse> newBooks = bookService.getPagedBooks(page, size, "", BookSortType.NEWEST);

		model.addAttribute("hotBooks", hotBooks);
		model.addAttribute("hotCategory", hotCategory);
		model.addAttribute("newBooks", newBooks);
		model.addAttribute("size", size);

		return "main";
	}
}