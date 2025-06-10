package shop.bluebooktle.backend.elasticsearch.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;
import shop.bluebooktle.backend.book.entity.BookCategory;
import shop.bluebooktle.backend.book.entity.BookPublisher;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.entity.BookTag;

@Getter
@Document(indexName = "#{@environment.getProperty('search.index.name')}")
@Setting(settingPath = "elasticsearch/book-setting.json")
@Mapping(mappingPath = "elasticsearch/book-mapping.json")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookDocument {

	@Id
	@Field(type = FieldType.Long)
	private Long id; // 도서 ID

	@Setter
	@Field(type = FieldType.Text)
	private String title; // 도서명

	@Setter
	@Field(type = FieldType.Text)
	private String description; // 도서설명

	@Setter
	@Field(type = FieldType.Date, format = {DateFormat.date_hour_minute_second_millis})
	private LocalDateTime publishDate; // 발행일

	@Setter
	@Field(type = FieldType.Scaled_Float, scalingFactor = 100) // 소수점 둘째 자리까지 저장
	private BigDecimal salePrice; // 판매가

	@Setter
	@Field(type = FieldType.Scaled_Float, scalingFactor = 10) // 소수점 첫째 자리까지 저장
	private BigDecimal star; // 평점

	@Setter
	@Field(type = FieldType.Long)
	private Long viewCount; // 조회수

	@Setter
	@Field(type = FieldType.Long)
	private Long searchCount; // 검색횟수

	@Setter
	@Field(type = FieldType.Long)
	private Long reviewCount; // 리뷰수

	@Setter
	@Field(type = FieldType.Keyword)
	private List<String> authorNames; // 작가 리스트

	@Setter
	@Field(type = FieldType.Keyword)
	private List<String> publisherNames; // 출판사 리스트

	@Setter
	@Field(type = FieldType.Keyword)
	private List<String> tagNames; // 태그 리스트

	@Setter
	@Field(type = FieldType.Long)
	private List<Long> categoryIds; // 카테고리 ID 리스트

	@Builder
	public BookDocument(Long id, String title, String description, LocalDateTime publishDate, BigDecimal salePrice,
		BigDecimal star, Long viewCount, Long searchCount, Long reviewCount, List<String> authorNames,
		List<String> publisherNames,
		List<String> tagNames, List<Long> categoryIds) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.publishDate = publishDate;
		this.salePrice = salePrice;
		this.star = star;
		this.viewCount = viewCount;
		this.searchCount = searchCount;
		this.reviewCount = reviewCount;
		this.authorNames = authorNames;
		this.publisherNames = publisherNames;
		this.tagNames = tagNames;
		this.categoryIds = categoryIds;
	}

	public static BookDocument from(Book book, BookSaleInfo bookSaleInfo, List<BookAuthor> bookAuthors,
		List<BookPublisher> bookPublishers, List<BookTag> bookTags, List<BookCategory> bookCategories) {
		return BookDocument.builder()
			.id(book.getId())
			.title(book.getTitle())
			.description(book.getDescription())
			.publishDate(book.getPublishDate())
			.salePrice(bookSaleInfo.getSalePrice())
			.star(bookSaleInfo.getStar())
			.viewCount(bookSaleInfo.getViewCount())
			.searchCount(bookSaleInfo.getSearchCount())
			.reviewCount(bookSaleInfo.getReviewCount())
			.authorNames(bookAuthors.stream().map(bookAuthor -> bookAuthor.getAuthor().getName()).toList())
			.publisherNames(
				bookPublishers.stream().map(bookPublisher -> bookPublisher.getPublisher().getName()).toList())
			.tagNames(bookTags.stream().map(bookTag -> bookTag.getTag().getName()).toList())
			.categoryIds(bookCategories.stream().map(bookCategory -> bookCategory.getCategory().getId()).toList())
			.build();
	}
}
