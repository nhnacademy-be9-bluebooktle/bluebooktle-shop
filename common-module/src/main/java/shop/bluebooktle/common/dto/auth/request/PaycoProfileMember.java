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
public class PaycoProfileMember {

	@JsonProperty("idNo")
	private String idNo;

	@JsonProperty("email")
	private String email;

	@JsonProperty("mobile")
	private String mobile;

	@JsonProperty("maskedEmail")
	private String maskedEmail;

	@JsonProperty("maskedMobile")
	private String maskedMobile;

	@JsonProperty("name")
	private String name;

	@JsonProperty("genderCode")
	private String genderCode;

	@JsonProperty("ageGroup")
	private String ageGroup;

	@JsonProperty("birthdayMMdd")
	private String birthdayMMdd;

	@JsonProperty("birthday")
	private String birthday;

	@JsonProperty("ci")
	private String ci;

	@JsonProperty("isForeigner")
	private String isForeigner;

	@JsonProperty("contactNumber")
	private String contactNumber;

	@JsonProperty("address")
	private Object address;
}