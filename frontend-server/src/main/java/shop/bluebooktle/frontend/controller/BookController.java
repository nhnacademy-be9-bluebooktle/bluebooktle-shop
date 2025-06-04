package shop.bluebooktle.frontend.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.response.BookDetailResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.frontend.service.BookService;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class BookController {

	private final BookService bookService;

	@GetMapping("/books")
	public String bookListPage(
		Model model,
		@RequestParam(required = false) String query,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "15") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

		Page<BookInfoResponse> pagedBooks = bookService.getPagedBooks(page, size, searchKeyword);
		log.info("pagedBooks: {}", pagedBooks);
		log.info("pagedBooks: {}", pagedBooks.getNumber());
		log.info("pagedBooks: {}", pagedBooks.getTotalElements());
		log.info("pagedBooks: {}", pagedBooks.getNumberOfElements());
		log.info("pagedBooks: {}", pagedBooks.getContent());

		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("size", size);
		model.addAttribute("filterCount", /* 실제 필터 개수 */ 0);
		model.addAttribute("pagedBooks", pagedBooks);

		return "book/book_list";
	}

	@GetMapping("/categories/{categoryId}")
	public String booksByCategoryPage(
		Model model,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "15") int size,
		@PathVariable Long categoryId
	) {
		log.info("요청된 카테고리 ID: {}", categoryId);
		Page<BookInfoResponse> pagedBooksByCategory = bookService.getPagedBooksByCategoryId(page, size, categoryId);
		CategoryResponse category = bookService.getCategoryById(categoryId);
		log.info("pagedBooksByCategory: {}", pagedBooksByCategory);
		log.info("category: {}", category);

		model.addAttribute("category", category);
		model.addAttribute("size", size);
		model.addAttribute("pagedBooks", pagedBooksByCategory);

		return "book/book_list";
	}

	// 도서 상세 페이지
	@GetMapping("/books/{bookId}")
	public String bookDetailPage(@PathVariable Long bookId,
		Model model,
		RedirectAttributes redirectAttributes) {
		log.info("도서 상세 조회 요청");
		try {
			BookDetailResponse book = bookService.getBookDetail(bookId);
			model.addAttribute("book", book);
			model.addAttribute("bookId", bookId);
			return "book/book_detail";
		} catch (Exception e) {
			log.error("도서 상세 조회 실패");
			redirectAttributes.addFlashAttribute("globalErrorMessage", "도서 정보 조회에 실패했습니다: " + e.getMessage());
			return "redirect:/books";
		}
	}
}
