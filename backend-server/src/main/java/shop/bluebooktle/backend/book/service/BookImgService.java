package shop.bluebooktle.backend.book.service;

import java.util.List;
import java.util.Optional;

import shop.bluebooktle.common.dto.book.request.BookImgRegisterRequest;
import shop.bluebooktle.common.dto.book.response.BookImgResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;

public interface BookImgService {

	// 도서 이미지 등록
	void registerBookImg(Long bookId, BookImgRegisterRequest bookImgRegisterRequest);

	// 도서 이미지 등록
	void registerBookImg(Long bookId, String imageUrl);

	// 특정 도서의 모든 이미지 목록 조회
	ImgResponse getImgByBookId(Long bookId);

	// 특정 이미지의 모든 도서 목록 조회
	List<BookInfoResponse> getBookByImgId(Long imgId);

	// 특정 도서의 썸네일 이미지 조회
	Optional<BookImgResponse> getThumbnailByBookId(Long bookId);

	// 도서 이미지 삭제
	void deleteBookImg(Long bookId, Long imgId);

	// 도서 이미지 수정
	void updateBookImg(Long bookId, String imageUrl);
}
