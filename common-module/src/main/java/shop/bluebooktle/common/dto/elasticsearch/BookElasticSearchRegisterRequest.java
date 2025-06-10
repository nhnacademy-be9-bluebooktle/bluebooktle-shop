package shop.bluebooktle.common.dto.elasticsearch;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookElasticSearchRegisterRequest {
	Long bookId;
	String title;
	String description;
	LocalDateTime publishDate;
	BigDecimal salePrice;
	BigDecimal star;
	Long viewCount;
	Long searchCount;
	Long reviewCount;
	List<String> authorNames;
	List<String> publisherNames;
	List<String> tagNames;
	List<Long> categoryIds;
}
