package shop.bluebooktle.backend.book.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class BookImgUpdateRequest {

	@NotBlank
	Long imgId;

	@NotBlank
	boolean isThumbnail;
}
