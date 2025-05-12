package shop.bluebooktle.backend.book.service;

import shop.bluebooktle.backend.book.dto.request.BookRegisterByAladinRequest;
import shop.bluebooktle.backend.book.dto.request.BookRegisterRequest;

public interface BookRegisterService {
	//책 직접등록
	void registerBook(BookRegisterRequest request);

	//알라딘api로 가져온 책 등록
	void registerBookByAladin(BookRegisterByAladinRequest request);
}
