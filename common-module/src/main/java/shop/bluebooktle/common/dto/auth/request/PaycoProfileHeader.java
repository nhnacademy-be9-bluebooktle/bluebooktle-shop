package shop.bluebooktle.common.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaycoProfileHeader {

	@JsonProperty("isSuccessful")
	private boolean isSuccessful;

	@JsonProperty("resultCode")
	private int resultCode;

	@JsonProperty("resultMessage")
	private String resultMessage;
}