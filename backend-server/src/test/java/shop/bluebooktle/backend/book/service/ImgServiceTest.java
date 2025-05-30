package shop.bluebooktle.backend.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book.service.impl.ImgServiceImpl;
import shop.bluebooktle.common.dto.book.request.img.ImgRegisterRequest;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
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
}
