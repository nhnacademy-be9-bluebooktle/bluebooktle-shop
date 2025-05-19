package shop.bluebooktle.backend.book.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class BookImgRegisterRequest {

	@NotBlank
	long imgId;

	@NotBlank
	String imgUrl;

	@NotBlank
	boolean isThumbnail;
}
