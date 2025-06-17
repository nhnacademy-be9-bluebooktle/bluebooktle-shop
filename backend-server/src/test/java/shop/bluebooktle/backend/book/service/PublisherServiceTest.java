package shop.bluebooktle.backend.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import shop.bluebooktle.backend.book.entity.Publisher;
import shop.bluebooktle.backend.book.repository.BookPublisherRepository;
import shop.bluebooktle.backend.book.repository.PublisherRepository;
import shop.bluebooktle.backend.book.service.impl.PublisherServiceImpl;
import shop.bluebooktle.backend.elasticsearch.service.BookElasticSearchService;
import shop.bluebooktle.common.dto.book.request.PublisherRequest;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.exception.book.PublisherAlreadyExistsException;
import shop.bluebooktle.common.exception.book.PublisherCannotDeleteException;
import shop.bluebooktle.common.exception.book.PublisherNotFoundException;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

	@Mock
	private PublisherRepository publisherRepository;

	@Mock
	private BookPublisherRepository bookPublisherRepository;

	@Mock
	private BookElasticSearchService bookElasticSearchService;

	@InjectMocks
	private PublisherServiceImpl publisherService;

	@Test
	@DisplayName("출판사 등록 성공")
	void registerPublisher_success() {
		// Given: 출판사 이름이 중복되지 않은 경우
		PublisherRequest request = new PublisherRequest("New Publisher");
		when(publisherRepository.existsByName(request.getName())).thenReturn(false);

		// When: registerPublisher 메서드를 호출할 때
		publisherService.registerPublisher(request);

		// Then: 새로운 출판사가 저장
		verify(publisherRepository, times(1)).save(any(Publisher.class));
	}

	@Test
	@DisplayName("출판사 등록 실패 - 출판사 이름중복")
	void registerPublisher_fail_alreadyExists() {
		// Given: 이미 존재하는 출판사 이름이 주어진 경우
		PublisherRequest request = new PublisherRequest("Existing Publisher");
		when(publisherRepository.existsByName(request.getName())).thenReturn(true);

		// When: registerPublisher 메서드를 호출할 때
		// Then: PublisherAlreadyExistsException 예외가 발생
		assertThrows(PublisherAlreadyExistsException.class, () -> publisherService.registerPublisher(request));
	}

	@Test
	@DisplayName("출판사 수정 성공")
	void updatePublisher_success() {
		// Given: 존재하는 출판사 ID와 새로운 이름이 주어진 경우
		Long publisherId = 1L;
		PublisherRequest request = new PublisherRequest("Updated Publisher");
		Publisher publisher = Publisher.builder().id(publisherId).name("Old Name").build();
		when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));
		doNothing().when(bookElasticSearchService).updatePublisherName(anyList(), anyString(), anyString());
		// When: updatePublisher 메서드를 호출할 때
		publisherService.updatePublisher(publisherId, request);

		// Then: 출판사의 이름이 업데이트되고 저장
		assertEquals("Updated Publisher", publisher.getName());
		verify(publisherRepository, times(1)).save(publisher);
	}

	@Test
	@DisplayName("출판사 수정 실패 - 출판사 존재X")
	void updatePublisher_fail_notFound() {
		// Given: 존재하지 않는 출판사 ID가 주어진 경우
		Long publisherId = 1L;
		PublisherRequest request = new PublisherRequest("Updated Publisher");
		when(publisherRepository.findById(publisherId)).thenReturn(Optional.empty());

		// When: updatePublisher 메서드를 호출할 때
		// Then: PublisherNotFoundException 예외가 발생
		assertThrows(PublisherNotFoundException.class, () -> publisherService.updatePublisher(publisherId, request));
	}

	@Test
	@DisplayName("출판사 수정 실패 - 출판사 이미 존재")
	void updatePublisher_fail_alreadyExists() {
		// Given
		Long publisherId = 1L;
		PublisherRequest request = new PublisherRequest("Updated Publisher");
		Publisher publisher = Publisher.builder().id(publisherId).name("Old Name").build();
		when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));
		when(publisherRepository.existsByName(request.getName())).thenReturn(true);

		// When: updatePublisher 메서드를 호출할 때
		// Then: PublisherNotFoundException 예외가 발생
		assertThrows(PublisherAlreadyExistsException.class,
			() -> publisherService.updatePublisher(publisherId, request));
	}

	@Test
	@DisplayName("출판사 조회 성공")
	void getPublisher_success() {
		// Given: 존재하는 출판사 ID가 주어진 경우
		Long publisherId = 1L;
		Publisher publisher = Publisher.builder().id(publisherId).name("Publisher Name").build();
		when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));

		// When: getPublisher 메서드를 호출할 때
		PublisherInfoResponse response = publisherService.getPublisher(publisherId);

		// Then: 올바른 출판사 정보가 반환
		assertEquals(publisherId, response.getId());
		assertEquals("Publisher Name", response.getName());
	}

	@Test
	@DisplayName("출판사 조회 실패 - 출판사 존재X")
	void getPublisher_fail_notFound() {
		// Given: 존재하지 않는 출판사 ID가 주어진 경우
		Long publisherId = 1L;
		when(publisherRepository.findById(publisherId)).thenReturn(Optional.empty());

		// When: getPublisher 메서드를 호출할 때
		// Then: PublisherNotFoundException 예외가 발생
		assertThrows(PublisherNotFoundException.class, () -> publisherService.getPublisher(publisherId));
	}

	@Test
	@DisplayName("출판사 목록 조회 성공")
	void getPublishers_success() {
		// Given: 출판사가 하나 이상 존재하는 경우
		PageRequest pageable = PageRequest.of(0, 10);
		Publisher publisher = Publisher.builder().id(1L).name("Publisher Name").build();
		Page<Publisher> publishers = new PageImpl<>(Collections.singletonList(publisher));
		when(publisherRepository.findAll(pageable)).thenReturn(publishers);

		// When: getPublishers 메서드를 호출할 때
		Page<PublisherInfoResponse> response = publisherService.getPublishers(pageable);

		// Then: 페이지 정보와 함께 출판사가 반환
		assertEquals(1, response.getTotalElements());
		assertEquals("Publisher Name", response.getContent().get(0).getName());
	}

	@Test
	@DisplayName("출판사 삭제 성공")
	void deletePublisher_success() {
		// Given: 존재하는 출판사 ID가 주어진 경우, 해당 출판사와 관련된 도서가 없는 경우
		Long publisherId = 1L;
		Publisher publisher = Publisher.builder().id(publisherId).name("Publisher Name").build();
		when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));
		when(bookPublisherRepository.existsByPublisher(publisher)).thenReturn(false);

		// When: deletePublisher 메서드를 호출할 때
		publisherService.deletePublisher(publisherId);

		// Then: 출판사가 삭제
		verify(publisherRepository, times(1)).delete(publisher);
	}

	@Test
	@DisplayName("출판사 삭제 실패 - 출판사에 연결된 도서 존재")
	void deletePublisher_fail_hasBooks() {
		// Given: 존재하는 출판사 ID가 주어진 경우, 해당 출판사에 관련된 도서가 있는 경우
		Long publisherId = 1L;
		Publisher publisher = Publisher.builder().id(publisherId).name("Publisher Name").build();
		when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));
		when(bookPublisherRepository.existsByPublisher(publisher)).thenReturn(true);

		// When: deletePublisher 메서드를 호출할 때
		// Then: PublisherCannotDeleteException 예외가 발생
		assertThrows(PublisherCannotDeleteException.class, () -> publisherService.deletePublisher(publisherId));
	}

	@Test
	@DisplayName("출판사 이름으로 등록 성공")
	void registerPublisherByName_success_newPublisher() {
		// Given: 새로운 출판사 이름이 주어진 경우
		String publisherName = "New Publisher";

		// 기존 출판사를 찾지 못 하는 경우
		when(publisherRepository.findByName(publisherName)).thenReturn(Optional.empty());

		// 새로운 출판사가 저장될 때 ID를 부여하여 반환
		when(publisherRepository.save(any(Publisher.class))).thenAnswer(invocation -> {
			Publisher publisher = invocation.getArgument(0, Publisher.class);
			return Publisher.builder()
				.id(1L)
				.name(publisher.getName())
				.build();
		});

		// When: registerPublisherByName 메서드를 호출할 때
		PublisherInfoResponse response = publisherService.registerPublisherByName(publisherName);

		// Then: 새로운 출판사가 생성되고 저장
		assertEquals(publisherName, response.getName());
		assertEquals(1L, response.getId());
		verify(publisherRepository, times(1)).save(any(Publisher.class));
	}

	@Test
	@DisplayName("출판사 이름으로 등록 실패 - 출판사 이름 이미 존재")
	void registerPublisherByName_success_existingPublisher() {
		// Given: 이미 존재하는 출판사 이름이 주어진 경우
		String publisherName = "Existing Publisher";
		Publisher publisher = Publisher.builder().id(1L).name(publisherName).build();
		when(publisherRepository.findByName(publisherName)).thenReturn(Optional.of(publisher));

		// When: registerPublisherByName 메서드를 호출할 때
		PublisherInfoResponse response = publisherService.registerPublisherByName(publisherName);

		// Then: 해당 출판사 정보가 반환되고 새로 저장되지 않음
		assertEquals(1L, response.getId());
		assertEquals(publisherName, response.getName());
		verify(publisherRepository, never()).save(any(Publisher.class));
	}

	@Test
	@DisplayName("검색어로 출판사 조회 - 성공")
	void searchPublishers_success() {
		String keyword = "졸음폭탄";
		PageRequest pageable = PageRequest.of(0, 10);
		Publisher publisher = Publisher.builder().id(1L).name("졸음폭탄출판사").build();
		Page<Publisher> publishers = new PageImpl<>(Collections.singletonList(publisher));
		when(publisherRepository.searchByNameContaining(keyword, pageable)).thenReturn(publishers);

		Page<PublisherInfoResponse> response = publisherService.searchPublishers(keyword, pageable);

		assertEquals(1, response.getTotalElements());
		assertEquals("졸음폭탄출판사", response.getContent().get(0).getName());
		verify(publisherRepository, times(1)).searchByNameContaining(keyword, pageable);
	}
}
