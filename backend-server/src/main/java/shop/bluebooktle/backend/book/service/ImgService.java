package shop.bluebooktle.backend.book.service;

import shop.bluebooktle.backend.book.dto.request.img.ImgRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.img.ImgUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.img.ImgResponse;

public interface ImgService {

	// 이미지 생성
	void registerImg(ImgRegisterRequest imgRegisterRequest);

	//이미지 조회
	ImgResponse getImg(Long imgId);

	// 이미지 수정
	void updateImg(Long imgId, ImgUpdateRequest imgUpdateRequest);

	// 이미지 삭제
	void deleteImg(Long imgId);

}
