package shop.bluebooktle.frontend.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookFormRequest;
import shop.bluebooktle.common.dto.book.request.BookUpdateRequest;
import shop.bluebooktle.common.dto.book.request.CategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.request.CategoryUpdateRequest;
import shop.bluebooktle.common.dto.book.request.RootCategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.response.AdminBookResponse;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.AdminAladinBookRepository;
import shop.bluebooktle.frontend.repository.AdminBookRepository;
import shop.bluebooktle.frontend.repository.BookImgRepository;
import shop.bluebooktle.frontend.repository.CategoryRepository;
import shop.bluebooktle.frontend.repository.ImageServerClient;
import shop.bluebooktle.frontend.repository.ImgRepository;
import shop.bluebooktle.frontend.service.impl.AdminBookServiceImpl;
import shop.bluebooktle.frontend.service.impl.AdminCategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AdminBookServiceTest {
	@Mock
	AdminBookRepository adminBookRepository;
	@Mock
	AdminImgService adminImgService;
	@Mock
	ImageServerClient imageServerClient;
	@Mock
	AdminAladinBookRepository adminAladinBookRepository;
	@Mock
	BookImgRepository bookImgRepository;
	@Mock
	ImgRepository imgRepository;

	@InjectMocks
	AdminBookServiceImpl bookService;

	@Test
	@DisplayName("도서 목록 조회 - 키워드 있음")
	void getPagedBooksByAdmin_withKeyword() {
		int page = 0, size = 10;
		String keyword = "테스트";
		List<AdminBookResponse> list = List.of(
			new AdminBookResponse(
				1L,
				"1234567890123",
				"제목",
				List.of("작가 A"),
				List.of("출판사 A"),
				BookSaleInfoState.AVAILABLE,
				new BigDecimal("9900"),
				10,
				LocalDateTime.now()
			)
		);
		PaginationData<AdminBookResponse> data = new PaginationData<>(
			list,
			new PaginationData.PaginationInfo(
				1,     // totalPages
				1L,    // totalElements
				0,     // currentPage
				10,    // pageSize
				true,  // isFirst
				true,  // isLast
				false, // hasNext
				false  // hasPrevious
			)
		);

		when(adminBookRepository.getPagedBooksByAdmin(page, size, keyword)).thenReturn(data);

		Page<AdminBookResponse> result = bookService.getPagedBooksByAdmin(page, size, keyword);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getTotalElements()).isEqualTo(1);
	}

	@Test
	@DisplayName("도서 목록 조회 - 키워드 없음 (null)")
	void getPagedBooksByAdmin_withNullKeyword() {
		int page = 0, size = 10;
		PaginationData<AdminBookResponse> data = new PaginationData<>(
			List.of(),
			new PaginationData.PaginationInfo(
				1,     // totalPages
				1L,    // totalElements
				0,     // currentPage
				10,    // pageSize
				true,  // isFirst
				true,  // isLast
				false, // hasNext
				false  // hasPrevious
			)
		);

		when(adminBookRepository.getPagedBooksByAdmin(page, size, null)).thenReturn(data);

		Page<AdminBookResponse> result = bookService.getPagedBooksByAdmin(page, size, null);

		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("도서 목록 조회 - 키워드가 공백일 경우")
	void getPagedBooksByAdmin_withBlankKeyword() {
		int page = 0, size = 10;
		String blankKeyword = "   ";

		PaginationData<AdminBookResponse> data = new PaginationData<>(
			List.of(),
			new PaginationData.PaginationInfo(
				1,     // totalPages
				1L,    // totalElements
				0,     // currentPage
				10,    // pageSize
				true,  // isFirst
				true,  // isLast
				false, // hasNext
				false  // hasPrevious
			)
		);

		when(adminBookRepository.getPagedBooksByAdmin(page, size, null)).thenReturn(data);

		Page<AdminBookResponse> result = bookService.getPagedBooksByAdmin(page, size, blankKeyword);

		assertThat(result).isEmpty();
		verify(adminBookRepository).getPagedBooksByAdmin(page, size, null);
	}

	@Test
	@DisplayName("도서 단건 조회 성공")
	void getBook_success() {
		BookAllResponse response = new BookAllResponse();
		when(adminBookRepository.getBook(1L)).thenReturn(response);

		BookAllResponse result = bookService.getBook(1L);

		assertThat(result).isNotNull();
		verify(adminBookRepository).getBook(1L);
	}

	@Test
	@DisplayName("직접 도서 등록 성공")
	void registerBook_success() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		when(file.getContentType()).thenReturn("image/png");
		when(file.getBytes()).thenReturn("bytes".getBytes());

		String fakeUrl = "http://localhost/bluebooktle-bookimage/test.jpg?token=abc";
		when(adminImgService.getPresignedUploadUrl()).thenReturn(fakeUrl);

		BookFormRequest form = new BookFormRequest(
			"책",                            // title
			"1234567890123",                // isbn
			"설명",                         // description
			"목차",                         // index
			LocalDate.now(),                // publishDate
			new BigDecimal("10000"),        // price
			new BigDecimal("9000"),         // salePrice
			10,                             // stock
			true,                           // isPackable
			BookSaleInfoState.AVAILABLE,      // state
			List.of(1L),                    // authorIdList
			List.of(2L),                    // publisherIdList
			List.of(3L),                    // categoryIdList
			List.of(4L),                    // tagIdList
			file                            // imageFile
		);

		bookService.registerBook(form);

		verify(adminBookRepository).registerBook(any(BookAllRegisterRequest.class));
		verify(imageServerClient).upload(any(), any(), anyMap(), anyMap(), any());
	}

	@Test
	@DisplayName("도서 삭제 성공")
	void deleteBook_success() {
		bookService.deleteBook(1L);
		verify(adminBookRepository).deleteBook(1L);
	}

	@Test
	@DisplayName("도서 수정 - 로컬 이미지 새로 업로드 (기존 이미지 삭제 포함)")
	void updateBook_withNewImageUploaded_fromLocal() throws Exception {
		// given
		MultipartFile file = mock(MultipartFile.class);
		when(file.isEmpty()).thenReturn(false);
		when(file.getContentType()).thenReturn("image/png");
		when(file.getBytes()).thenReturn("data".getBytes());

		ImgResponse img = new ImgResponse(1L, "/images/old.jpg", LocalDateTime.now());
		when(bookImgRepository.getImgsByBookId(1L)).thenReturn(img);

		when(adminImgService.getPresignedUploadUrl())
			.thenReturn("http://localhost/bluebooktle-bookimage/new.jpg?token=abc");

		BookUpdateRequest request = BookUpdateRequest.builder()
			.title("수정")
			.description("수정 설명")
			.index("목차 수정")
			.publishDate(LocalDate.now())
			.price(new BigDecimal("10000"))
			.salePrice(new BigDecimal("9000"))
			.stock(10)
			.isPackable(true)
			.state(BookSaleInfoState.AVAILABLE)
			.authorIdList(List.of(1L))
			.publisherIdList(List.of(2L))
			.categoryIdList(List.of(3L))
			.tagIdList(List.of(4L))
			.imageFile(file)
			.isAladinImg(false) // ✅ 필드명 정확히
			.build();

		// when
		bookService.updateBook(1L, request);

		// then
		verify(adminImgService).deleteImage("old.jpg");
		verify(imgRepository).deleteImg(1L);
		verify(adminBookRepository).updateBook(eq(1L), any());
		verify(imageServerClient).upload(any(), any(), anyMap(), anyMap(), any());
	}

	@Test
	@DisplayName("도서 수정 - 이미지 없음")
	void updateBook_withoutImage() {
		BookUpdateRequest request = BookUpdateRequest.builder()
			.title("수정")
			.imageFile(null)
			.isAladinImg(true)
			.build();

		bookService.updateBook(1L, request);
		verify(adminBookRepository).updateBook(eq(1L), any());
	}

	@Test
	@DisplayName("도서 수정 - MultipartFile은 있지만 실제로 비어 있는 경우")
	void updateBook_withImageButEmpty_shouldNotUpload() {
		// given
		MultipartFile file = mock(MultipartFile.class);
		when(file.isEmpty()).thenReturn(true);

		BookUpdateRequest request = BookUpdateRequest.builder()
			.title("수정")
			.description("설명")
			.index("목차")
			.publishDate(LocalDate.now())
			.price(new BigDecimal("10000"))
			.salePrice(new BigDecimal("9000"))
			.stock(5)
			.isPackable(false)
			.state(BookSaleInfoState.AVAILABLE)
			.authorIdList(List.of(1L))
			.publisherIdList(List.of(2L))
			.categoryIdList(List.of(3L))
			.tagIdList(List.of(4L))
			.imageFile(file)
			.isAladinImg(false)
			.build();

		// when
		bookService.updateBook(1L, request);

		// then
		verify(adminBookRepository).updateBook(eq(1L), any());
		verifyNoInteractions(bookImgRepository, adminImgService, imgRepository, imageServerClient);
	}

	@Test
	@DisplayName("도서 수정 - 알라딘 이미지 사용, 기존 이미지 삭제 없이 업로드만")
	void updateBook_withAladinImg_shouldSkipDelete() throws Exception {
		// given
		MultipartFile file = mock(MultipartFile.class);
		when(file.isEmpty()).thenReturn(false);
		when(file.getContentType()).thenReturn("image/jpeg");
		when(file.getBytes()).thenReturn("mockdata".getBytes());

		when(adminImgService.getPresignedUploadUrl())
			.thenReturn("http://localhost/bluebooktle-bookimage/newfile.jpg?token=abc");

		BookUpdateRequest request = BookUpdateRequest.builder()
			.title("수정")
			.description("설명")
			.index("목차")
			.publishDate(LocalDate.now())
			.price(new BigDecimal("12000"))
			.salePrice(new BigDecimal("10000"))
			.stock(5)
			.isPackable(false)
			.state(BookSaleInfoState.AVAILABLE)
			.authorIdList(List.of(1L))
			.publisherIdList(List.of(2L))
			.categoryIdList(List.of(3L))
			.tagIdList(List.of(4L))
			.imageFile(file)
			.isAladinImg(true)
			.build();

		// when
		bookService.updateBook(1L, request);

		// then
		verify(adminBookRepository).updateBook(eq(1L), any());
		verifyNoInteractions(bookImgRepository, imgRepository); // 삭제 없음
		verify(imageServerClient).upload(any(), any(), anyMap(), anyMap(), any());
	}

	@Test
	@DisplayName("MinIO 업로드 중 IOException 발생 시 UncheckedIOException으로 전환")
	void uploadToMinio_throwsUncheckedIOException() throws Exception {
		// given
		MultipartFile file = mock(MultipartFile.class);
		when(file.getContentType()).thenReturn("image/jpeg");
		when(file.getBytes()).thenThrow(new IOException("파일 읽기 실패"));

		when(adminImgService.getPresignedUploadUrl())
			.thenReturn("http://localhost/bluebooktle-bookimage/test.jpg?token=abc");

		BookUpdateRequest request = BookUpdateRequest.builder()
			.title("에러")
			.description("에러설명")
			.index("목차")
			.publishDate(LocalDate.now())
			.price(new BigDecimal("10000"))
			.salePrice(new BigDecimal("8000"))
			.stock(3)
			.isPackable(true)
			.state(BookSaleInfoState.AVAILABLE)
			.authorIdList(List.of(1L))
			.publisherIdList(List.of(2L))
			.categoryIdList(List.of(3L))
			.tagIdList(List.of(4L))
			.imageFile(file)
			.isAladinImg(true)
			.build();

		// when & then
		assertThatThrownBy(() -> bookService.updateBook(1L, request))
			.isInstanceOf(UncheckedIOException.class)
			.hasMessageContaining("MinIO 업로드 실패");

		verifyNoInteractions(bookImgRepository, imgRepository); // 삭제 전 실패
	}


	@Test
	@DisplayName("알라딘 기반 도서 등록 성공")
	void registerBookByAladin_success() {
		BookAllRegisterByAladinRequest request = BookAllRegisterByAladinRequest.builder()
			.isbn("1234567890123")
			.index("목차")
			.stock(10)
			.isPackable(true)
			.state(BookSaleInfoState.AVAILABLE)
			.categoryIdList(List.of(1L, 2L))
			.tagIdList(List.of(3L, 4L))
			.build();
		bookService.registerBookByAladin(request);
		verify(adminAladinBookRepository).registerAladinBook(request);
	}

	@Test
	@DisplayName("알라딘 도서 검색 성공")
	void searchAladin_success() {
		when(adminAladinBookRepository.searchBooks("파울로", 1, 10))
			.thenReturn(List.of(new AladinBookResponse()));
		List<AladinBookResponse> result = bookService.searchAladin("파울로", 1, 10);
		assertThat(result).hasSize(1);
	}
}
