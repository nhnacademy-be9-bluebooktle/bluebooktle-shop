package shop.bluebooktle.backend.book.dto.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import shop.bluebooktle.backend.book.dto.response.img.ImgResponse;

@Value
@Builder
@Jacksonized
public class BookImgResponse {

	@JsonUnwrapped
	ImgResponse imgResponse;

	BookInfoResponse bookInfoResponse;

	Boolean isThumbnail;
}
