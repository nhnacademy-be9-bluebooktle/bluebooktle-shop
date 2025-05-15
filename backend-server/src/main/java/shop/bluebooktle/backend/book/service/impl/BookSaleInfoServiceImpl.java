package shop.bluebooktle.backend.book.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookSaleInfoUpdateRequest;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.service.BookSaleInfoService;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookSaleInfoNotFoundException;

@Service
@RequiredArgsConstructor
public class BookSaleInfoServiceImpl implements BookSaleInfoService {

	// 아직수정필요

	private final BookSaleInfoRepository booksaleinfoRepository;

	@Override
	public BookSaleInfo save(BookSaleInfo bookSaleInfo) {
		if (bookSaleInfo == null) {
			throw new BookSaleInfoNotFoundException("도서 판매 정보가 존재하지 않습니다.");
		}
		return booksaleinfoRepository.save(bookSaleInfo);
	}

	@Override
	@Transactional(readOnly = true)
	public BookSaleInfo findById(Long id) {
		return booksaleinfoRepository.findById(id)
			.orElseThrow(() -> new BookSaleInfoNotFoundException("도서 판매 정보를 찾을 수 없습니다."));
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookSaleInfo> findAll() {
		return booksaleinfoRepository.findAll();
	}

	@Override
	public void deleteById(Long id) {
		if (!booksaleinfoRepository.existsById(id)) {
			throw new BookSaleInfoNotFoundException("도서 판매 정보를 찾을 수 없습니다.");
		}
		booksaleinfoRepository.deleteById(id);
	}

	@Override
	@Transactional
	public BookSaleInfo update(Long id, BookSaleInfoUpdateRequest request) {
		if (id == null) {
			throw new BookSaleInfoNotFoundException("도서 판매 정보 ID가 필요합니다.");
		}
		if (request == null) {
			throw new BookSaleInfoNotFoundException("도서 판매 정보 요청이 필요합니다.");
		}

		BookSaleInfo existingSaleInfo = booksaleinfoRepository.findById(id)
			.orElseThrow(() -> new BookSaleInfoNotFoundException("도서 판매 정보 ID: " + id + "를 찾을 수 없습니다."));

		BookSaleInfo updatedSaleInfo = request.toEntity(existingSaleInfo);

		return booksaleinfoRepository.save(updatedSaleInfo);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<BookSaleInfo> findByBook(Book book) {
		if (book == null || book.getId() == null) {
			throw new BookNotFoundException("도서를 찾을 수 없습니다.");
		}
		return booksaleinfoRepository.findByBook(book);
	}
}