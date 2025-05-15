package shop.bluebooktle.backend.book.service;

import shop.bluebooktle.backend.book.dto.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.backend.book.dto.request.BookAllRegisterRequest;

public interface BookRegisterService {
	//책 직접등록
	void registerBook(BookAllRegisterRequest request);

	//알라딘api로 가져온 책 등록
	void registerBookByAladin(BookAllRegisterByAladinRequest request);
}
