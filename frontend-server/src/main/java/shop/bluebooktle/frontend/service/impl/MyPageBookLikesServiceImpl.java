package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.response.BookLikesListResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.MyPageBookLikesRepository;
import shop.bluebooktle.frontend.service.MyPageBookLikesService;

@Service
@RequiredArgsConstructor
public class MyPageBookLikesServiceImpl implements MyPageBookLikesService {
	private final MyPageBookLikesRepository myPageBookLikesRepository;

	@Override
	public PaginationData<BookLikesListResponse> getMyPageBookLikes(int page, int size) {
		// 전체 좋아요 목록 조회
		List<BookLikesListResponse> likesList = myPageBookLikesRepository.getMyPageBookLikes();

		// 페이지 처리
		Pageable pageable = PageRequest.of(page, size);

		// List 전체에서 부분 리스트를 잘라서 PageImpl
		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), likesList.size());
		List<BookLikesListResponse> content = (start < end) ? likesList.subList(start, end) : List.of();

		Page<BookLikesListResponse> pageImpl = new PageImpl<>(content, pageable, likesList.size());

		return new PaginationData<>(pageImpl);
	}

	@Override
	public void unlike(Long bookId) {
		myPageBookLikesRepository.unlike(bookId);
	}
}
