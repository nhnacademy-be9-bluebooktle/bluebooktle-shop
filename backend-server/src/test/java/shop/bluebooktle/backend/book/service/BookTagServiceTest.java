package shop.bluebooktle.backend.book.service;

import static org.assertj.core.api.Assertions.*;  // 변경: AssertionsForClassTypes → Assertions
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookTag;
import shop.bluebooktle.backend.book.entity.Tag;
import shop.bluebooktle.backend.book.repository.BookTagRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.TagRepository;
import shop.bluebooktle.backend.book.service.impl.BookTagServiceImpl;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookTagAlreadyExistsException;
import shop.bluebooktle.common.exception.book.TagNotFoundException;

@ExtendWith(MockitoExtension.class)
public class BookTagServiceTest {

	@Mock
	private BookTagRepository bookTagRepository;
	@Mock
	private TagRepository tagRepository;
	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private BookTagServiceImpl bookTagService;

	private Book dummyBook() {
		Book b = mock(Book.class);
		return b;
	}

	private Tag dummyTag(Long id, String name) {
		Tag t = mock(Tag.class);
		when(t.getId()).thenReturn(id);
		when(t.getName()).thenReturn(name);
		when(t.getCreatedAt()).thenReturn(LocalDateTime.of(2020,1,1,0,0));
		return t;
	}

	@Test
	@DisplayName("단일 태그 등록 성공")
	void registerBookTag_success() {
		var book = dummyBook();
		var tag  = dummyTag(22L, "T1");

		when(bookRepository.findById(11L)).thenReturn(Optional.of(book));
		when(tagRepository.findById(22L)).thenReturn(Optional.of(tag));
		when(bookTagRepository.existsByBookAndTag(book, tag)).thenReturn(false);

		var resp = bookTagService.registerBookTag(11L, 22L);

		verify(bookTagRepository).save(argThat((BookTag bt) ->
			bt.getBook() == book && bt.getTag() == tag
		));

		assertThat(resp.getId()).isEqualTo(22L);
		assertThat(resp.getName()).isEqualTo("T1");
		assertThat(resp.getCreatedAt()).isEqualTo(LocalDateTime.of(2020,1,1,0,0));
	}

	@Test
	@DisplayName("단일 태그 등록 실패 - 책이 없으면 예외")
	void registerBookTag_noBook() {
		when(bookRepository.findById(99L)).thenReturn(Optional.empty());
		assertThrows(BookNotFoundException.class, () ->
			bookTagService.registerBookTag(99L, 1L)
		);
		verifyNoInteractions(tagRepository, bookTagRepository);
	}

	@Test
	@DisplayName("단일 태그 등록 실패 - 태그가 없으면 예외")
	void registerBookTag_noTag() {
		when(bookRepository.findById(11L)).thenReturn(Optional.of(dummyBook()));
		when(tagRepository.findById(33L)).thenReturn(Optional.empty());
		assertThrows(TagNotFoundException.class, () ->
			bookTagService.registerBookTag(11L, 33L)
		);
		verify(bookTagRepository, never()).save(any());
	}

	@Test
	@DisplayName("단일 태그 등록 실패 - 이미 존재하면 예외")
	void registerBookTag_alreadyExists() {
		// given
		Book book = dummyBook();
		Tag tag  = new Tag("태그");
		// 이 테스트에선 이름/생성일 stub 이 실제로 필요 없으면 빼셔도 됩니다.

		when(bookRepository.findById(11L)).thenReturn(Optional.of(book));
		when(tagRepository.findById(22L)).thenReturn(Optional.of(tag));
		// <-- 여기를 true 로
		when(bookTagRepository.existsByBookAndTag(book, tag)).thenReturn(true);

		// then
		assertThrows(BookTagAlreadyExistsException.class, () ->
			bookTagService.registerBookTag(11L, 22L)
		);
		verify(bookTagRepository, never()).save(any());
	}


	@Test
	@DisplayName("여러 태그 등록 성공")
	void registerBookTag_list_success() {
		var book = dummyBook();
		when(bookRepository.findById(11L)).thenReturn(Optional.of(book));

		var tags = List.of(dummyTag(101L, "A"), dummyTag(102L, "B"));
		tags.forEach(t -> {
			when(tagRepository.findById(t.getId())).thenReturn(Optional.of(t));
			when(bookTagRepository.existsByBookAndTag(book, t)).thenReturn(false);
		});

		var resps = bookTagService.registerBookTag(11L, List.of(101L, 102L));

		assertThat(resps).extracting(TagInfoResponse::getId)
			.containsExactly(101L, 102L);
		verify(bookTagRepository, times(2)).save(any());
	}

	@Test
	@DisplayName("태그 일괄 업데이트: 기존 삭제 후 재등록")
	void updateBookTag_success() {
		// 기존 매핑 3개
		var existing = List.of(mock(BookTag.class), mock(BookTag.class), mock(BookTag.class));
		when(bookTagRepository.findByBookId(11L)).thenReturn(existing);

		// 새 태그 ID들
		var newTags = List.of(201L, 202L);
		var book = dummyBook();
		when(bookRepository.findById(11L)).thenReturn(Optional.of(book));

		newTags.forEach(id -> {
			var t = dummyTag(id, "T"+id);
			when(tagRepository.findById(id)).thenReturn(Optional.of(t));
			when(bookTagRepository.existsByBookAndTag(book, t)).thenReturn(false);
		});

		List<TagInfoResponse> out = bookTagService.updateBookTag(11L, newTags);

		// 기존 삭제
		verify(bookTagRepository).deleteAll(existing);
		// 새 등록된 ID 순서대로 비교
		assertThat(out.stream()
			.map(TagInfoResponse::getId)
			.collect(Collectors.toList()))
			.containsExactlyElementsOf(newTags);
	}
}
