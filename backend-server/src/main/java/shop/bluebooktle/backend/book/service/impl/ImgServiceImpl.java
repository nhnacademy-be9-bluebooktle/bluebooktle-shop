package shop.bluebooktle.backend.book.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.img.ImgRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.img.ImgUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.img.ImgResponse;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book.service.ImgService;

@Service
@RequiredArgsConstructor
public class ImgServiceImpl implements ImgService {

	private final ImgRepository imgRepository;

	@Transactional
	@Override
	public void registerImg(ImgRegisterRequest imgRegisterRequest) {
		if (imgRegisterRequest.getImgUrl() == null || imgRegisterRequest.getImgUrl().trim().isEmpty()) {
			throw new IllegalArgumentException("ImgUrl is empty"); // TODO: 프로젝트 에러 처리 방식으로 변경
		}
		if (!imgRepository.findByImgUrl(imgRegisterRequest.getImgUrl()).isEmpty()) {
			throw new IllegalArgumentException("Img already exists"); // TODO: 프로젝트 예외 처리 방식
		}

		Img img = Img.builder().imgUrl(imgRegisterRequest.getImgUrl()).build();

		imgRepository.save(img);

	}

	@Transactional(readOnly = true)
	@Override
	public ImgResponse getImg(Long imgId) {
		if (imgId == null) {
			throw new IllegalArgumentException("ImgId is empty");
		}

		Img img = imgRepository.findById(imgId).orElseThrow(() -> new IllegalArgumentException("Img not found"));

		ImgResponse imgResponse = ImgResponse.builder()
			.id(img.getId())
			.imgUrl(img.getImgUrl())
			.build();

		return imgResponse;
	}

	@Transactional
	@Override
	public void updateImg(Long imgId, ImgUpdateRequest imgUpdateRequest) {
		if (imgId == null) {
			throw new IllegalArgumentException("ImgId is empty");
		}
		if (imgUpdateRequest.getImgUrl() == null || imgUpdateRequest.getImgUrl().trim().isEmpty()) {
			throw new IllegalArgumentException("ImgUrl is empty");
		}

		Img img = imgRepository.findById(imgId).orElseThrow(() -> new IllegalArgumentException("Img not found"));

		img.setImgUrl(imgUpdateRequest.getImgUrl());

		imgRepository.save(img);
	}

	@Transactional
	@Override
	public void deleteImg(Long imgId) {
		if (imgId == null) {
			throw new IllegalArgumentException("ImgId is empty");
		}
		imgRepository.deleteById(imgId);
	}
}
