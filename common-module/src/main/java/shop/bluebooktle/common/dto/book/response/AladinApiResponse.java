package shop.bluebooktle.common.dto.book.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;
import shop.bluebooktle.common.dto.book.request.AladinBookItem;

// 테스트 편의를 위해 @Builder 추가

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinApiResponse {
	private List<AladinBookItem> item;
}
