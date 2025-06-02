package shop.bluebooktle.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class BookController {

	@GetMapping("/books")
	public String bookListPage(@RequestParam(required = false) String query,
		@RequestParam(required = false) String category) {
		return "book/book_list";
	}

	@GetMapping("/categories/{categoryId}")
	public String booksByCategoryPage(@PathVariable String categoryId) {

		System.out.println("요청된 카테고리 ID: " + categoryId);
		return "book/book_list";
	}

	@GetMapping("/books/{bookId}")
	public String bookDetailPage(@PathVariable Long bookId) {
		System.out.println("요청된 도서 ID: " + bookId);
		return "book/book_detail";
	}
}
