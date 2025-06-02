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
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.AdminAladinBookRepository;
import shop.bluebooktle.frontend.repository.AdminBookRepository;
import shop.bluebooktle.frontend.repository.AdminImgUploadRepository;
import shop.bluebooktle.frontend.service.AdminBookService;
import shop.bluebooktle.frontend.service.AdminImgService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminBookServiceImpl implements AdminBookService {
	private final AdminBookRepository adminBookRepository;
	private final AdminImgService adminImgService;
	private final AdminImgUploadRepository adminImgUploadRepository;
	private final AdminAladinBookRepository adminAladinBookRepository;

	@Override
	public Page<BookAllResponse> getPagedBooks(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size);

		String keyword = null;
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			keyword = searchKeyword;
		}
		PaginationData<BookAllResponse> data = adminBookRepository.getPagedBooks(page, size, keyword);
		List<BookAllResponse> categories = data.getContent();
		return new PageImpl<>(categories, pageable, data.getTotalElements());
	}

	@Override
	public BookAllResponse getBook(Long bookId) {
		return adminBookRepository.getBook(bookId);
	}

	@Override
	public void registerBook(BookFormRequest bookFormRequest) {

		MultipartFile file = bookFormRequest.getImageFile();
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

		log.info("{}", file.getContentType());
		// 3) Feign 호출
		try {
			adminImgUploadRepository.upload(
				bucket,
				objectName,
				queryParams,
				headers,
				file.getBytes()
			);
		} catch (IOException e) {
			throw new UncheckedIOException("MinIO 업로드 실패", e);
		}

		BookAllRegisterRequest request = BookAllRegisterRequest.builder()
			.title(bookFormRequest.getTitle())
			.isbn(bookFormRequest.getIsbn())
			.description(bookFormRequest.getDescription())
			.index(bookFormRequest.getIndex())
			.publishDate(bookFormRequest.getPublishDate())
			.price(bookFormRequest.getPrice())
			.salePrice(bookFormRequest.getSalePrice())
			.stock(bookFormRequest.getStock())
			.state(bookFormRequest.getState())
			.authorIdList(bookFormRequest.getAuthorIdList())
			.publisherIdList(bookFormRequest.getPublisherIdList())
			.categoryIdList(bookFormRequest.getCategoryIdList())
			.tagIdList(bookFormRequest.getTagIdList())
			.imgUrl(presignedUploadUrl.split("\\?")[0])
			.build();

		adminBookRepository.registerBook(request);
	}

	@Override
	public void deleteBook(Long bookId) {
		adminBookRepository.deleteBook(bookId);
	}

	@Override
	public void registerBookByAladin(BookAllRegisterByAladinRequest request) {
		adminAladinBookRepository.registerAladinBook(request);
	}

	@Override
	public List<AladinBookResponse> searchAladin(String keyword, int page, int size) {
		return adminAladinBookRepository.searchBooks(keyword, page, size);
	}
}
