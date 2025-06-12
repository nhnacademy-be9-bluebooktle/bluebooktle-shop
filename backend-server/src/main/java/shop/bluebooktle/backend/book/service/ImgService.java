package shop.bluebooktle.backend.book.service;

import shop.bluebooktle.common.dto.book.request.img.ImgRegisterRequest;
import shop.bluebooktle.common.dto.book.request.img.ImgUpdateRequest;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;

public interface ImgService {

	// 이미지 생성
	void registerImg(ImgRegisterRequest imgRegisterRequest);

	//이미지 조회
	ImgResponse getImg(Long imgId);

	// 이미지 수정
	void updateImg(Long imgId, ImgUpdateRequest imgUpdateRequest);

	// 이미지 삭제
	void deleteImg(Long imgId);

	ImgResponse getImgByReviewId(Long reviewId);

}
