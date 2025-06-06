package shop.bluebooktle.frontend.service.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookFormRequest;
import shop.bluebooktle.common.dto.book.request.BookUpdateRequest;
import shop.bluebooktle.common.dto.book.request.BookUpdateServiceRequest;
import shop.bluebooktle.common.dto.book.response.AdminBookResponse;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.AdminAladinBookRepository;
import shop.bluebooktle.frontend.repository.AdminBookRepository;
import shop.bluebooktle.frontend.repository.BookImgRepository;
import shop.bluebooktle.frontend.repository.ImageServerClient;
import shop.bluebooktle.frontend.repository.ImgRepository;
import shop.bluebooktle.frontend.service.AdminBookService;
import shop.bluebooktle.frontend.service.AdminImgService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminBookServiceImpl implements AdminBookService {
	private final AdminBookRepository adminBookRepository;
	private final AdminImgService adminImgService;
	private final ImageServerClient imageServerClient;
	private final AdminAladinBookRepository adminAladinBookRepository;
	private final BookImgRepository bookImgRepository;
	private final ImgRepository imgRepository;

	@Override
	public Page<BookInfoResponse> getPagedBooks(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size);

		String keyword = null;
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			keyword = searchKeyword;
		}
		PaginationData<BookInfoResponse> data = adminBookRepository.getPagedBooks(page, size, keyword);
		List<BookInfoResponse> categories = data.getContent();
		return new PageImpl<>(categories, pageable, data.getTotalElements());
	}

	@Override
	public Page<AdminBookResponse> getPagedBooksByAdmin(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size);

		String keyword = null;
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			keyword = searchKeyword;
		}
		PaginationData<AdminBookResponse> data = adminBookRepository.getPagedBooksByAdmin(page, size, keyword);
		List<AdminBookResponse> categories = data.getContent();
		return new PageImpl<>(categories, pageable, data.getTotalElements());
	}

	@Override
	public BookAllResponse getBook(Long bookId) {
		return adminBookRepository.getBook(bookId);
	}

	@Override
	public void registerBook(BookFormRequest bookFormRequest) {
		MultipartFile file = bookFormRequest.getImageFile();
		String uploadedUrl = uploadToMinio(file);
		String imageKey = uploadedUrl.substring(uploadedUrl.indexOf("/bluebooktle-bookimage"));
		log.info("imageKey : {}", imageKey);

		BookAllRegisterRequest request = BookAllRegisterRequest.builder()
			.title(bookFormRequest.getTitle())
			.isbn(bookFormRequest.getIsbn())
			.description(bookFormRequest.getDescription())
			.index(bookFormRequest.getIndex())
			.publishDate(bookFormRequest.getPublishDate())
			.price(bookFormRequest.getPrice())
			.salePrice(bookFormRequest.getSalePrice())
			.stock(bookFormRequest.getStock())
			.isPackable(bookFormRequest.getIsPackable())
			.state(bookFormRequest.getState())
			.authorIdList(bookFormRequest.getAuthorIdList())
			.publisherIdList(bookFormRequest.getPublisherIdList())
			.categoryIdList(bookFormRequest.getCategoryIdList())
			.tagIdList(bookFormRequest.getTagIdList())
			.imgUrl("/images" + imageKey)
			.build();

		adminBookRepository.registerBook(request);
	}

	@Override
	public void deleteBook(Long bookId) {
		adminBookRepository.deleteBook(bookId);
	}

	@Override
	public void updateBook(Long bookId, BookUpdateRequest bookUpdateRequest) {
		// 알라딘 등록 도서가 이미지 수정하는 경우 : 미니오 서버에 저장
		String imageKey = "";
		if (bookUpdateRequest.isAladinImg() && bookUpdateRequest.getImageFile() != null) {
			MultipartFile file = bookUpdateRequest.getImageFile();
			String uploadedUrl = uploadToMinio(file);
			imageKey = uploadedUrl.substring(uploadedUrl.indexOf("/bluebooktle-bookimage"));

		} else if (bookUpdateRequest.getImageFile() != null) {
			ImgResponse image = bookImgRepository.getImgsByBookId(bookId);
			String imageUrl = image.getImgUrl();
			log.info("imageUrl : {}", imageUrl);
			String objectName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

			// 기존 이미지 미니오에서 삭제
			adminImgService.deleteImage(objectName);
			log.info("기존 이미지 미니오에서 삭제");
			// 기존 이미지 삭제
			imgRepository.deleteImg(image.getId());

			// 미니오 등록
			MultipartFile file = bookUpdateRequest.getImageFile();
			String uploadedUrl = uploadToMinio(file);
			imageKey = uploadedUrl.substring(uploadedUrl.indexOf("/bluebooktle-bookimage"));

		}
		BookUpdateServiceRequest updateServiceRequest =
			new BookUpdateServiceRequest(
				bookUpdateRequest.getTitle(),
				bookUpdateRequest.getDescription(),
				bookUpdateRequest.getIndex(),
				bookUpdateRequest.getPublishDate(),
				bookUpdateRequest.getPrice(),
				bookUpdateRequest.getSalePrice(),
				bookUpdateRequest.getStock(),
				bookUpdateRequest.getIsPackable(),
				bookUpdateRequest.getState(),
				bookUpdateRequest.getAuthorIdList(),
				bookUpdateRequest.getPublisherIdList(),
				bookUpdateRequest.getCategoryIdList(),
				bookUpdateRequest.getTagIdList(),
				"/images" + imageKey
			);

		adminBookRepository.updateBook(bookId, updateServiceRequest);
	}

	@Override
	public void registerBookByAladin(BookAllRegisterByAladinRequest request) {
		adminAladinBookRepository.registerAladinBook(request);
	}

	@Override
	public List<AladinBookResponse> searchAladin(String keyword, int page, int size) {
		return adminAladinBookRepository.searchBooks(keyword, page, size);
	}

	private String uploadToMinio(MultipartFile file) {
		String presignedUploadUrl = adminImgService.getPresignedUploadUrl();
		URI uri = URI.create(presignedUploadUrl);

		// 1) path 분해
		String[] segments = uri.getPath().split("/", 3);
		String bucket = segments[1];
		String objectName = segments[2];

		// 2) 쿼리 파라미터 분해
		Map<String, String> queryParams = Arrays.stream(uri.getQuery().split("&"))
			.map(s -> s.split("=", 2))
			.collect(Collectors.toMap(
				kv -> URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
				kv -> URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
			));
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", file.getContentType());

		try {
			imageServerClient.upload(
				bucket,
				objectName,
				queryParams,
				headers,
				file.getBytes()
			);
		} catch (IOException e) {
			throw new UncheckedIOException("MinIO 업로드 실패", e);
		}

		log.info("업로드 완료 → URL: {}", presignedUploadUrl.split("\\?")[0]);
		return presignedUploadUrl.split("\\?")[0]; // 순수 URL만 리턴
	}
}
