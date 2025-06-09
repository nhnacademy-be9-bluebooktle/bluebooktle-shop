package shop.bluebooktle.backend.elasticsearch.service;

import java.util.List;

import org.springframework.data.domain.Page;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.elasticsearch.BookElasticSearchRegisterRequest;
import shop.bluebooktle.common.dto.elasticsearch.BookElasticSearchUpdateRequest;

public interface BookElasticSearchService {

	// 엘라스틱 도서 등록
	void registerBook(BookElasticSearchRegisterRequest request);

	// 도서 검색 시 검색횟수 증가
	void updateSearchCount(List<Book> bookList);

	// 도서 상세 페이지 접근 시 조회수 증가
	void updateViewCount(Book book);

	// TODO 리뷰 등록 시 리뷰수 증가 및 평점 수정

	// 도서 수정 시 엘라스틱 도서 수정
	void updateBook(BookElasticSearchUpdateRequest request);

	// 태그명 수정시 엘라스틱 도서 수정
	void updateTagName(List<Book> bookList, String updatedTagName, String currentTagName);

	// 작가명 수정시 엘라스틱 도서 수정
	void updateAuthorName(List<Book> bookList, String updatedAuthorName, String currentAuthorName);

	// 출판사명 수정시 엘라스틱 도서 수정
	void updatePublisherName(List<Book> bookList, String updatePublisherName, String currentPublisherName);

	// 엘라스틱 도서 정보 삭제
	void deleteBook(Book book);

	// 키워드로 도서 검색 (관리자 페이지)
	Page<Book> searchBooksByKeyword(String keyword, int page, int size);

	// 도서 정렬
	Page<Book> searchBooksBySortOnly(BookSortType bookSortType, int page, int size);

	// 키워드 및 도서 정렬
	Page<Book> searchBooksByKeywordAndSort(String keyword, BookSortType bookSortType, int page, int size);

	// 카테고리 및 도서 정렬
	Page<Book> searchBooksByCategoryAndSort(List<Long> categoryIds, BookSortType bookSortType, int page,
		int size);

}
