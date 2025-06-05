package shop.bluebooktle.common.dto.book.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true) // 모든 필드를 강제로 초기화해서 기본 생성자 생성
public class BookDetailResponse {
	private String isbn;
	private String title; // 책 제목
	private List<String> authors; // 작가 목록
	private List<String> publishers; // 출판사 목록
	//TODO 평점 추가
	//TODO 리뷰 개수 추가
	private BigDecimal price; // 정가
	private BigDecimal salePrice; // 판매 가격
	private Integer salePercentage; // 할인율
	private String description; // 도서 소개
	private String index; // 도서 목차
	private String imgUrl; // 이미지
	private BookSaleInfoState saleState; // 판매 상태
}
