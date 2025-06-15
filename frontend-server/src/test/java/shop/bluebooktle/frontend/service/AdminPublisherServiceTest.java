package shop.bluebooktle.frontend.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.request.PublisherRequest;
import shop.bluebooktle.common.dto.book.request.TagRequest;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.exception.book.PublisherCreateException;
import shop.bluebooktle.common.exception.book.PublisherDeleteException;
import shop.bluebooktle.common.exception.book.PublisherListFetchException;
import shop.bluebooktle.common.exception.book.PublisherNotFoundException;
import shop.bluebooktle.common.exception.book.PublisherUpdateException;
import shop.bluebooktle.common.exception.book.TagCreateException;
import shop.bluebooktle.common.exception.book.TagDeleteException;
import shop.bluebooktle.common.exception.book.TagListFetchException;
import shop.bluebooktle.common.exception.book.TagNotFoundException;
import shop.bluebooktle.common.exception.book.TagUpdateException;
import shop.bluebooktle.frontend.repository.AdminPublisherRepository;
import shop.bluebooktle.frontend.repository.AdminTagRepository;
import shop.bluebooktle.frontend.service.impl.AdminPublisherServiceImpl;
import shop.bluebooktle.frontend.service.impl.AdminTagServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AdminPublisherServiceTest {

	@Mock
	AdminPublisherRepository publisherRepository;

	@InjectMocks
	AdminPublisherServiceImpl publisherService;

	@Test
	@DisplayName("출판사 목록 조회 - 키워드 O")
	void getPublishers_withKeyword() {
		// given
		int page = 0, size = 10;
		String keyword = "출판사";
		List<PublisherInfoResponse> list = List.of(
			new PublisherInfoResponse(1L, "출판사", LocalDateTime.now())
		);
		PaginationData<PublisherInfoResponse> mockData = new PaginationData<>(
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
		when(publisherRepository.getPublishers(page, size, keyword)).thenReturn(mockData);

		// when
		Page<PublisherInfoResponse> result = publisherService.getPublishers(page, size, keyword);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().getName()).isEqualTo("출판사");
	}

	@Test
	@DisplayName("출판사 목록 조회 - 키워드 null")
	void getPublishers_nullKeyword() {
		int page = 0, size = 10;
		List<PublisherInfoResponse> list = List.of(
			new PublisherInfoResponse(1L, "전체출판", LocalDateTime.now())
		);
		PaginationData<PublisherInfoResponse> mockData = new PaginationData<>(
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
		when(publisherRepository.getPublishers(page, size, null)).thenReturn(mockData);

		Page<PublisherInfoResponse> result = publisherService.getPublishers(page, size, null);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().getName()).isEqualTo("전체출판");
	}

	@Test
	@DisplayName("출판사 목록 조회 - 키워드 빈 문자열")
	void getPublishers_blankKeyword() {
		int page = 0, size = 10;
		List<PublisherInfoResponse> list = List.of(
			new PublisherInfoResponse(2L, "빈문자출판", LocalDateTime.now())
		);
		PaginationData<PublisherInfoResponse> mockData = new PaginationData<>(
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
		when(publisherRepository.getPublishers(page, size, null)).thenReturn(mockData);

		Page<PublisherInfoResponse> result = publisherService.getPublishers(page, size, " ");

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().getName()).isEqualTo("빈문자출판");
	}

	@Test
	@DisplayName("출판사 목록 조회 실패 시 예외 발생")
	void getPublishers_exceptionThrown() {
		when(publisherRepository.getPublishers(anyInt(), anyInt(), any())).thenThrow(RuntimeException.class);

		assertThatThrownBy(() -> publisherService.getPublishers(0, 10, "test"))
			.isInstanceOf(PublisherListFetchException.class);
	}

	@Test
	@DisplayName("출판사 단건 조회 성공")
	void getPublisher_success() {
		PublisherInfoResponse response = new PublisherInfoResponse(1L, "문학출판", LocalDateTime.now());
		when(publisherRepository.getPublisher(1L)).thenReturn(response);

		PublisherInfoResponse result = publisherService.getPublisher(1L);

		assertThat(result.getName()).isEqualTo("문학출판");
	}

	@Test
	@DisplayName("출판사 단건 조회 실패")
	void getPublisher_exceptionThrown() {
		when(publisherRepository.getPublisher(1L)).thenThrow(RuntimeException.class);

		assertThatThrownBy(() -> publisherService.getPublisher(1L))
			.isInstanceOf(PublisherNotFoundException.class);
	}

	@Test
	@DisplayName("출판사 생성 성공")
	void createPublisher_success() {
		PublisherRequest request = new PublisherRequest("새출판사");
		publisherService.createPublisher(request);

		verify(publisherRepository, times(1)).createPublisher(request);
	}

	@Test
	@DisplayName("출판사 생성 실패")
	void createPublisher_exceptionThrown() {
		PublisherRequest request = new PublisherRequest("에러출판사");
		doThrow(RuntimeException.class).when(publisherRepository).createPublisher(request);

		assertThatThrownBy(() -> publisherService.createPublisher(request))
			.isInstanceOf(PublisherCreateException.class);
	}

	@Test
	@DisplayName("출판사 수정 성공")
	void updatePublisher_success() {
		PublisherRequest request = new PublisherRequest("수정출판사");
		publisherService.updatePublisher(1L, request);

		verify(publisherRepository, times(1)).updatePublisher(1L, request);
	}

	@Test
	@DisplayName("출판사 수정 실패")
	void updatePublisher_exceptionThrown() {
		PublisherRequest request = new PublisherRequest("실패출판사");
		doThrow(RuntimeException.class).when(publisherRepository).updatePublisher(1L, request);

		assertThatThrownBy(() -> publisherService.updatePublisher(1L, request))
			.isInstanceOf(PublisherUpdateException.class);
	}

	@Test
	@DisplayName("출판사 삭제 성공")
	void deletePublisher_success() {
		publisherService.deletePublisher(1L);
		verify(publisherRepository, times(1)).deletePublisher(1L);
	}

	@Test
	@DisplayName("출판사 삭제 실패")
	void deletePublisher_exceptionThrown() {
		doThrow(RuntimeException.class).when(publisherRepository).deletePublisher(1L);

		assertThatThrownBy(() -> publisherService.deletePublisher(1L))
			.isInstanceOf(PublisherDeleteException.class);
	}
}
