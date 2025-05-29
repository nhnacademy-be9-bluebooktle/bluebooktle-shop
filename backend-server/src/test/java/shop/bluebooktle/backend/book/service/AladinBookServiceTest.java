package shop.bluebooktle.backend.book.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.book.adapter.AladinAdaptor;
import shop.bluebooktle.backend.book.service.impl.AladinBookServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AladinBookServiceTest {

	@Mock
	private AladinAdaptor aladinAdaptor;

	@InjectMocks
	private AladinBookServiceImpl aladinBookService;

	// 01
	// AladinApiResponse가 AladinBookResponse로 정확하게 변환되는지 확인
	@Test
	@DisplayName("알라딘 API 응답 -> 내부 응답 객체 변환 성공")
	void searchBooks_Success() {

	}

	// getItem()이 빈 리스트인 경우

	// aladinAdaptor.searchBooks(query)이 null인 경우

	// 02
	// 리스트에 값이 있는 경우

	// 빈 리스트인 경우

	// List<AladinBookResponse> books = searchBooks(isbn)에서 예외가 생기는 경우

}
