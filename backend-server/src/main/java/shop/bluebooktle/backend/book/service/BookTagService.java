package shop.bluebooktle.backend.book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;

public interface BookTagService {
	// 도서 태그 등록
	TagInfoResponse registerBookTag(Long bookId, Long tagId);

	List<TagInfoResponse> registerBookTag(Long bookId, List<Long> tagIdList);

	List<TagInfoResponse> updateBookTag(Long bookId, List<Long> tagIdList);
}
