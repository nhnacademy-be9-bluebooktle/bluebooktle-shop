package shop.bluebooktle.backend.book.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.AladinBookDto;
import shop.bluebooktle.backend.book.service.AladinBookService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aladin")
public class AladinBookController {

	private final AladinBookService aladinBookService;

	@GetMapping("/search")
	public List<AladinBookDto> searchBooks(@RequestParam String query) {
		return aladinBookService.searchBooks(query);
	}
}
