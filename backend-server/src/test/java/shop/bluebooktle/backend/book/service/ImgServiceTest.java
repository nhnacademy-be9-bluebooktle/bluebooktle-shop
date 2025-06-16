package shop.bluebooktle.backend.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book.service.impl.ImgServiceImpl;
import shop.bluebooktle.backend.review.entity.Review;
import shop.bluebooktle.backend.review.repository.ReviewRepository;
import shop.bluebooktle.common.dto.book.request.img.ImgRegisterRequest;
import shop.bluebooktle.common.dto.book.request.img.ImgUpdateRequest;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.exception.InvalidInputValueException;
import shop.bluebooktle.common.exception.book.ImgAlreadyExistsException;
import shop.bluebooktle.common.exception.book.ImgIdNullException;
import shop.bluebooktle.common.exception.book.ImgNotFoundException;
import shop.bluebooktle.common.exception.book.ImgUrlEmptyException;

@ExtendWith(MockitoExtension.class)
public class ImgServiceTest {

	@InjectMocks
	private ImgServiceImpl imgService;

	@Mock
	private ImgRepository imgRepository;
	@Mock
	private ReviewRepository reviewRepository;

	@Test
	@DisplayName("이미지 등록 성공")
	void registerImg_success() {
		ImgRegisterRequest imgRegisterRequest = ImgRegisterRequest.builder()
			.imgUrl("imgUrl")
			.build();

		when(imgRepository.findByImgUrl(imgRegisterRequest.getImgUrl())).thenReturn(Optional.empty());

		imgService.registerImg(imgRegisterRequest);

		verify(imgRepository, times(1)).save(any(Img.class));
	}

	@Test
	@DisplayName("이미지 등록 실패 - url이 null인 경우")
	void registerImg_fail_imgUrl_null() {
		ImgRegisterRequest imgRegisterRequest = ImgRegisterRequest.builder()
			.imgUrl(null)
			.build();

		assertThrows(ImgUrlEmptyException.class, () -> {
			imgService.registerImg(imgRegisterRequest);
		});

		verify(imgRepository, never()).save(any(Img.class));
	}

	@Test
	@DisplayName("이미지 등록 실패 - url이 빈 칸인 경우")
	void registerImg_fail_imgUrl_blank() {
		ImgRegisterRequest imgRegisterRequest = ImgRegisterRequest.builder()
			.imgUrl(" ")
			.build();

		assertThrows(ImgUrlEmptyException.class, () -> {
			imgService.registerImg(imgRegisterRequest);
		});

		verify(imgRepository, never()).save(any(Img.class));
	}

	@Test
	@DisplayName("이미지 등록 실패 - url이 이미 존재하는 경우")
	void registerImg_fail_imgUrl_duplicate() {
		ImgRegisterRequest imgRegisterRequest = ImgRegisterRequest.builder()
			.imgUrl("imgUrl")
			.build();

		Img img = Img.builder()
			.imgUrl("imgUrl")
			.build();

		when(imgRepository.findByImgUrl(imgRegisterRequest.getImgUrl())).thenReturn(Optional.of(img));

		assertThrows(ImgAlreadyExistsException.class, () ->
			imgService.registerImg(imgRegisterRequest)
		);

		verify(imgRepository, never()).save(any(Img.class));
	}

	@Test
	@DisplayName("이미지 조회 성공")
	void getImg_success() {
		Long imgId = 1L;
		Img img = Img.builder().build();
		ReflectionTestUtils.setField(img, "id", imgId);

		when(imgRepository.findById(imgId)).thenReturn(Optional.of(img));

		ImgResponse imgResponse = imgService.getImg(imgId);

		assertNotNull(imgResponse);
		assertEquals(imgId, imgResponse.getId());
	}

	@Test
	@DisplayName("이미지 조회 실패 - 이미지 id가 null인 경우")
	void getImg_fail_imgId_null() {
		assertThrows(ImgIdNullException.class, () ->
			imgService.getImg(null));
	}

	@Test
	@DisplayName("이미지 조회 실패 - 유효한 이미지 id가 아닌 경우")
	void getImg_fail_imgId_invalid() {
		Long imgId = 999L;

		when(imgRepository.findById(imgId)).thenReturn(Optional.empty());

		assertThrows(ImgNotFoundException.class, () ->
			imgService.getImg(imgId));
	}

	@Test
	@DisplayName("이미지 수정 성공")
	void updateImg_success() {
		Long id = 2L;
		Img old = Img.builder().imgUrl("old.png").build();
		ReflectionTestUtils.setField(old, "id", id);

		when(imgRepository.findById(id)).thenReturn(Optional.of(old));

		ImgUpdateRequest req = ImgUpdateRequest.builder()
			.imgUrl("new.png").build();

		imgService.updateImg(id, req);

		// save 호출 시 바뀐 url 이 반영되었는지
		ArgumentCaptor<Img> captor = ArgumentCaptor.forClass(Img.class);
		verify(imgRepository).save(captor.capture());
		assertEquals("new.png", captor.getValue().getImgUrl());
	}

	@Test
	@DisplayName("이미지 수정 실패 - url이 null인 경우")
	void updateImg_fail_imgUrl_null() {
		ImgUpdateRequest req = ImgUpdateRequest.builder()
			.imgUrl(null)
			.build();

		assertThrows(ImgUrlEmptyException.class, () ->
			imgService.updateImg(1L, req)
		);
		verify(imgRepository, never()).save(any());
	}

	@Test
	@DisplayName("이미지 수정 실패 - url이 빈 칸인 경우")
	void updateImg_fail_imgUrl_blank() {
		ImgUpdateRequest req = ImgUpdateRequest.builder()
			.imgUrl("   ")
			.build();

		assertThrows(ImgUrlEmptyException.class, () ->
			imgService.updateImg(1L, req)
		);
		verify(imgRepository, never()).save(any());
	}

	@Test
	@DisplayName("이미지 수정 실패 - 존재하지 않는 imgId인 경우")
	void updateImg_fail_imgNotFound() {
		ImgUpdateRequest req = ImgUpdateRequest.builder()
			.imgUrl("new.png")
			.build();

		when(imgRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ImgNotFoundException.class, () ->
			imgService.updateImg(1L, req)
		);
		verify(imgRepository, never()).save(any());
	}

	@Test
	@DisplayName("이미지 삭제 성공")
	void deleteImg_success() {
		Long id = 3L;
		imgService.deleteImg(id);
		verify(imgRepository).deleteById(id);
	}

	@Test
	@DisplayName("이미지 삭제 실패 - ID null")
	void deleteImg_fail() {
		assertThrows(ImgIdNullException.class, () -> imgService.deleteImg(null));
		verify(imgRepository, never()).deleteById(any());
	}

	// --- getImgByReviewId ---

	@Test
	@DisplayName("리뷰 이미지 조회 - reviewId null")
	void getImgByReviewId_fail_null() {
		assertThrows(InvalidInputValueException.class, () -> imgService.getImgByReviewId(null));
	}

	@Test
	@DisplayName("리뷰 이미지 조회 - review not found")
	void getImgByReviewId_fail_notFound() {
		when(reviewRepository.findById(7L)).thenReturn(Optional.empty());
		assertThrows(InvalidInputValueException.class, () -> imgService.getImgByReviewId(7L));
	}

	@Test
	@DisplayName("리뷰 이미지 조회 - 리뷰에 이미지 없음")
	void getImgByReviewId_noImage() {
		Review r = mock(Review.class);
		when(reviewRepository.findById(8L)).thenReturn(Optional.of(r));
		// r.getImg() 기본 null
		ImgResponse res = imgService.getImgByReviewId(8L);
		assertNull(res.getId());
		assertNull(res.getImgUrl());
		assertNull(res.getCreatedAt());
	}

	@Test
	@DisplayName("리뷰 이미지 조회 - 이미지가 있는 경우")
	void getImgByReviewId_withImage() {
		Img img = Img.builder().imgUrl("r.png").build();
		ReflectionTestUtils.setField(img, "id", 42L);
		LocalDateTime created = LocalDateTime.ofInstant(Instant.now(), java.time.ZoneId.systemDefault());
		ReflectionTestUtils.setField(img, "createdAt", created);

		Review r = mock(Review.class);
		when(r.getImg()).thenReturn(img);
		when(reviewRepository.findById(9L)).thenReturn(Optional.of(r));

		ImgResponse res = imgService.getImgByReviewId(9L);
		assertEquals(42L, res.getId());
		assertEquals("r.png", res.getImgUrl());
		assertEquals(created, res.getCreatedAt());
	}
}
