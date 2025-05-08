package shop.bluebooktle.backend.book.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.response.AladinBookResponseDto;
import shop.bluebooktle.backend.book.service.AladinBookService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aladin") //일단 임의로 설정해놓음
public class AladinBookController {

	private final AladinBookService aladinBookService;

	@GetMapping("/search")
	public List<AladinBookResponseDto> searchBooks(@RequestParam String query) {
		return aladinBookService.searchBooks(query);
	}

	//책 등록 구현해야함
	// public ResponseEntity<String> createBook(@RequestBody BookCreateRequest bookCreateRequest) {}

}
