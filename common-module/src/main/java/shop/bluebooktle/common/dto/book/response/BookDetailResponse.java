package shop.bluebooktle.common.dto.book.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true) // 모든 필드를 강제로 초기화해서 기본 생성자 생성
public class BookDetailResponse {
	String isbn;
	String title; // 책 제목
	List<String> authors; // 작가 목록
	List<String> publishers; // 출판사 목록
	//TODO 평점 추가
	//TODO 리뷰 개수 추가
	BigDecimal price; // 정가
	BigDecimal salePrice; // 판매 가격
	String description; // 도서 소개
	String index; // 도서 목차
}
