package shop.bluebooktle.backend.book.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.review.entity.Review;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.review.repository.ReviewRepository;
import shop.bluebooktle.backend.book.service.ImgService;
import shop.bluebooktle.common.dto.book.request.img.ImgRegisterRequest;
import shop.bluebooktle.common.dto.book.request.img.ImgUpdateRequest;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.exception.InvalidInputValueException;
import shop.bluebooktle.common.exception.book.ImgAlreadyExistsException;
import shop.bluebooktle.common.exception.book.ImgIdNullException;
import shop.bluebooktle.common.exception.book.ImgNotFoundException;
import shop.bluebooktle.common.exception.book.ImgUrlEmptyException;

@Service
@Transactional
@RequiredArgsConstructor
public class ImgServiceImpl implements ImgService {

	private final ImgRepository imgRepository;
	private final ReviewRepository reviewRepository;

	@Override
	public void registerImg(ImgRegisterRequest imgRegisterRequest) {
		if (imgRegisterRequest.getImgUrl() == null || imgRegisterRequest.getImgUrl().trim().isEmpty()) {
			throw new ImgUrlEmptyException();
		}
		if (imgRepository.findByImgUrl(imgRegisterRequest.getImgUrl()).isPresent()) {
			throw new ImgAlreadyExistsException(imgRegisterRequest.getImgUrl());
		}

		Img img = Img.builder().imgUrl(imgRegisterRequest.getImgUrl()).build();

		imgRepository.save(img);

	}

	@Transactional(readOnly = true)
	@Override
	public ImgResponse getImg(Long imgId) {
		if (imgId == null) {
			throw new ImgIdNullException();
		}

		Img img = imgRepository.findById(imgId).orElseThrow(() -> new ImgNotFoundException());

		ImgResponse imgResponse = ImgResponse.builder()
			.id(img.getId())
			.imgUrl(img.getImgUrl())
			.build();

		return imgResponse;
	}

	@Override
	public void updateImg(Long imgId, ImgUpdateRequest imgUpdateRequest) {
		if (imgId == null) {
			throw new ImgIdNullException();
		}
		if (imgUpdateRequest.getImgUrl() == null || imgUpdateRequest.getImgUrl().trim().isEmpty()) {
			throw new ImgUrlEmptyException();
		}

		Img img = imgRepository.findById(imgId).orElseThrow(() -> new ImgNotFoundException());

		img.setImgUrl(imgUpdateRequest.getImgUrl());

		imgRepository.save(img);
	}

	@Override
	public void deleteImg(Long imgId) {
		if (imgId == null) {
			throw new ImgIdNullException();
		}
		imgRepository.deleteById(imgId);
	}

	@Override
	@Transactional(readOnly = true)
	public ImgResponse getImgByReviewId(Long reviewId) {
		if (reviewId == null) {
			throw new InvalidInputValueException();
		}

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(InvalidInputValueException::new);

		Img reviewImg = review.getImg();

		if (reviewImg == null) {
			return ImgResponse.builder()
				.id(null)
				.imgUrl(null)
				.createdAt(null)
				.build();
		}

		return ImgResponse.builder()
			.id(reviewImg.getId())
			.imgUrl(reviewImg.getImgUrl())
			.createdAt(reviewImg.getCreatedAt())
			.build();
	}
}
