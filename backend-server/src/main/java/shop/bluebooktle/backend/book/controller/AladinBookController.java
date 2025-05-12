package shop.bluebooktle.backend.book.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookRegisterByAladinRequest;
import shop.bluebooktle.backend.book.dto.response.AladinBookResponseDto;
import shop.bluebooktle.backend.book.service.AladinBookService;
import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.common.exception.book.AladinBookNotFoundException;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/books") //일단 임의로 설정해놓음
public class AladinBookController {

	private final AladinBookService aladinBookService;
	private final BookRegisterService bookRegisterService;

	// 알라딘 API 도서 검색
	@GetMapping("/search")
	public List<AladinBookResponseDto> searchBooks(@RequestParam String query) {
		return aladinBookService.searchBooks(query);
	}

	// isbn으로 도서정보 가져오기
	@GetMapping("/select")
	public AladinBookResponseDto selectBook(@RequestParam String isbn) {
		AladinBookResponseDto book = aladinBookService.getBookByIsbn(isbn);
		if (book == null) {
			throw new AladinBookNotFoundException(isbn);
		}
		return book;
	}

	// 알라딘 api로 도서 등록
	@PostMapping("/register/aladin")
	public ResponseEntity<Void> registerAladinBook(@RequestBody BookRegisterByAladinRequest request) {
		bookRegisterService.registerBookByAladin(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
