package shop.bluebooktle.backend.book.service;

import shop.bluebooktle.common.dto.book.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;

public interface BookRegisterService {
	//책 모든정보 직접등록
	void registerBook(BookAllRegisterRequest request);

	//알라딘api로 가져온 책 모든정보 등록
	void registerBookByAladin(BookAllRegisterByAladinRequest request);
}
