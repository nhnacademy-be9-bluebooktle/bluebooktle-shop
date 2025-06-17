package shop.bluebooktle.backend.user.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.user.service.AddressService;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.dto.user.request.AddressRequest;
import shop.bluebooktle.common.dto.user.response.AddressResponse;
import shop.bluebooktle.common.entity.auth.Address;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.InvalidTokenException;
import shop.bluebooktle.common.exception.user.AddressNotFoundException;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.security.UserPrincipal;
import shop.bluebooktle.common.util.JwtUtil;

@ActiveProfiles("test")
@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = true)
class AddressControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AddressService addressService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("내 주소 목록 조회 성공 (200)")
	void getUserAddresses_success() throws Exception {

		UserDto userDto = UserDto.builder()
			.id(1L)
			.loginId("loginId")
			.nickname("nickname")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		UserPrincipal userPrincipal = new UserPrincipal(userDto);

		Address address = Address.builder()
			.alias("alias")
			.roadAddress("roadAddress")
			.detailAddress("detailAddress")
			.postalCode("postalCode")
			.build();
		ReflectionTestUtils.setField(address, "id", 1L);

		AddressResponse addressResponse = AddressResponse.builder()
			.addressId(address.getId())
			.alias(address.getAlias())
			.roadAddress(address.getRoadAddress())
			.detailAddress(address.getDetailAddress())
			.postalCode(address.getPostalCode())
			.build();

		List<AddressResponse> addresses = List.of(addressResponse);

		given(addressService.findAddressesByUserId(userPrincipal.getUserId()))
			.willReturn(addresses);

		mockMvc.perform(get("/api/addresses")
				.with(user(userPrincipal)))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].addressId").value(1L));

		verify(addressService).findAddressesByUserId(userPrincipal.getUserId());
	}

	@Test
	@DisplayName("내 주소 목록 조회 실패 (401) - 로그인 되지 않은 사용자")
	void getUserAddresses_fail_401() throws Exception {

		mockMvc.perform(get("/api/addresses"))
			.andExpect(status().isUnauthorized());

		verifyNoInteractions(addressService);
	}

	@Test
	@DisplayName("내 주소 등록 성공 (201)")
	void createAddress_success() throws Exception {

		UserDto userDto = UserDto.builder()
			.id(1L)
			.loginId("loginId")
			.nickname("nickname")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		UserPrincipal userPrincipal = new UserPrincipal(userDto);

		AddressRequest addressRequest = AddressRequest.builder()
			.alias("alias")
			.roadAddress("roadAddress")
			.detailAddress("detailAddress")
			.postalCode("12345")
			.build();

		Address address = Address.builder()
			.alias(addressRequest.getAlias())
			.roadAddress(addressRequest.getRoadAddress())
			.detailAddress(addressRequest.getDetailAddress())
			.postalCode(addressRequest.getPostalCode())
			.build();
		ReflectionTestUtils.setField(address, "id", 1L);

		AddressResponse addressResponse = AddressResponse.fromEntity(address);

		given(addressService.createAddress(eq(userPrincipal.getUserId()), any(AddressRequest.class)))
			.willReturn(addressResponse);

		mockMvc.perform(post("/api/addresses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(addressRequest))
				.with(user(userPrincipal))
				.with(csrf()))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.addressId").value(1L));

		verify(addressService).createAddress(eq(userPrincipal.getUserId()), any(AddressRequest.class));
	}

	@Test
	@DisplayName("내 주소 등록 실패 (400) - 필수 필드 누락")
	void createAddress_fail_400() throws Exception {
		UserDto userDto = UserDto.builder()
			.id(1L)
			.loginId("loginId")
			.nickname("nickname")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		UserPrincipal userPrincipal = new UserPrincipal(userDto);

		AddressRequest addressRequest = AddressRequest.builder()
			.alias("alias")
			.roadAddress("roadAddress")
			.detailAddress("detailAddress")
			.build();

		mockMvc.perform(post("/api/addresses")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(addressRequest))
			.with(user(userPrincipal))
			.with(csrf()))
			.andExpect(status().isBadRequest());

		verifyNoInteractions(addressService);
	}

	@Test
	@DisplayName("내 주소 등록 실패 (401) - 로그인 되지 않은 사용자")
	void createAddress_fail_401() throws Exception {

		mockMvc.perform(post("/api/addresses")
			.with(csrf()))
			.andExpect(status().isUnauthorized());

		verifyNoInteractions(addressService);
	}

	@Test
	@DisplayName("내 주소 수정 성공 (200)")
	void updateAddress_success() throws Exception {

		User user = User.builder()
			.id(1L)
			.loginId("loginId")
			.nickname("nickname")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		UserDto userDto = UserDto.builder()
			.id(user.getId())
			.loginId(user.getLoginId())
			.nickname(user.getNickname())
			.type(user.getType())
			.status(user.getStatus())
			.build();

		UserPrincipal userPrincipal = new UserPrincipal(userDto);

		Address currentAddress = Address.builder()
			.alias("currentAlias")
			.roadAddress("currentRoadAddress")
			.detailAddress("currentDetailAddress")
			.postalCode("12345")
			.build();
		ReflectionTestUtils.setField(currentAddress, "id", 1L);
		ReflectionTestUtils.setField(currentAddress, "user", user);

		AddressRequest addressRequest = AddressRequest.builder()
			.alias("updatedAlias")
			.roadAddress("updatedRoadAddress")
			.detailAddress("updatedDetailAddress")
			.postalCode("67890")
			.build();

		Address updatedAddress = Address.builder()
			.alias(addressRequest.getAlias())
			.roadAddress(addressRequest.getRoadAddress())
			.detailAddress(addressRequest.getDetailAddress())
			.postalCode(addressRequest.getPostalCode())
			.build();
		ReflectionTestUtils.setField(updatedAddress, "id", 1L);

		AddressResponse addressResponse = AddressResponse.fromEntity(updatedAddress);

		given(addressService.updateAddress(eq(userPrincipal.getUserId()), anyLong(), any(AddressRequest.class)))
			.willReturn(addressResponse);

		mockMvc.perform(put("/api/addresses/{addressId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(addressRequest))
				.with(user(userPrincipal))
				.with(csrf()))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.addressId").value(1L))
			.andExpect(jsonPath("$.data.alias").value("updatedAlias"));

		verify(addressService).updateAddress(eq(userPrincipal.getUserId()), anyLong(), any(AddressRequest.class));
	}

	@Test
	@DisplayName("내 주소 수정 실패 (400) - 필수 필드 누락")
	@WithMockUser
	void updateAddress_fail_400() throws Exception {

		User user = User.builder()
			.id(1L)
			.loginId("loginId")
			.nickname("nickname")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		UserDto userDto = UserDto.builder()
			.id(user.getId())
			.loginId(user.getLoginId())
			.nickname(user.getNickname())
			.type(user.getType())
			.status(user.getStatus())
			.build();

		UserPrincipal userPrincipal = new UserPrincipal(userDto);

		Address currentAddress = Address.builder()
			.alias("currentAlias")
			.roadAddress("currentRoadAddress")
			.detailAddress("currentDetailAddress")
			.postalCode("12345")
			.build();
		ReflectionTestUtils.setField(currentAddress, "id", 1L);
		ReflectionTestUtils.setField(currentAddress, "user", user);

		AddressRequest addressRequest = AddressRequest.builder()
			.alias("updatedAlias")
			.roadAddress("updatedRoadAddress")
			.detailAddress("updatedDetailAddress")
			.build();

		Address updatedAddress = Address.builder()
			.alias(addressRequest.getAlias())
			.roadAddress(addressRequest.getRoadAddress())
			.detailAddress(addressRequest.getDetailAddress())
			.build();

		mockMvc.perform(put("/api/addresses/{addressId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(addressRequest))
				.with(user(userPrincipal))
				.with(csrf()))
			.andExpect(status().isBadRequest());

		verifyNoInteractions(addressService);
	}

	@Test
	@DisplayName("내 주소 수정 실패 (401) - 로그인 되지 않은 사용자")
	void updateAddress_fail_401() throws Exception {

		mockMvc.perform(put("/api/addresses/{addressId}", 1L)
				.with(csrf()))
			.andExpect(status().isUnauthorized());

		verifyNoInteractions(addressService);

	}

	@Test
	@DisplayName("내 주소 수정 실패 (404) - 존재하지 않는 주소")
	@WithMockUser
	void updateAddress_fail_404() throws Exception {
			User user = User.builder()
				.id(1L)
				.loginId("loginId")
				.nickname("nickname")
				.type(UserType.USER)
				.status(UserStatus.ACTIVE)
				.build();

			UserDto userDto = UserDto.builder()
				.id(user.getId())
				.loginId(user.getLoginId())
				.nickname(user.getNickname())
				.type(user.getType())
				.status(user.getStatus())
				.build();

			UserPrincipal userPrincipal = new UserPrincipal(userDto);

			AddressRequest addressRequest = AddressRequest.builder()
				.alias("updatedAlias")
				.roadAddress("updatedRoadAddress")
				.detailAddress("updatedDetailAddress")
				.postalCode("67890")
				.build();

			Address updatedAddress = Address.builder()
				.alias(addressRequest.getAlias())
				.roadAddress(addressRequest.getRoadAddress())
				.detailAddress(addressRequest.getDetailAddress())
				.postalCode(addressRequest.getPostalCode())
				.build();
			ReflectionTestUtils.setField(updatedAddress, "id", 1L);

			AddressResponse addressResponse = AddressResponse.fromEntity(updatedAddress);

			Long missingAddressId = 99L;

			given(addressService.updateAddress(eq(userPrincipal.getUserId()), eq(missingAddressId), any(AddressRequest.class)))
				.willThrow(new AddressNotFoundException("수정할 주소를 찾을 수 없습니다."));

			mockMvc.perform(put("/api/addresses/{addressId}", missingAddressId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(addressRequest))
					.with(user(userPrincipal))
					.with(csrf()))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value("error"));

			verify(addressService).updateAddress(eq(userPrincipal.getUserId()), eq(missingAddressId), any(AddressRequest.class));
		}

	@Test
	@DisplayName("내 주소 삭제 성공 (200)")
	void deleteAddress_success() throws Exception {

		UserDto userDto = UserDto.builder()
			.id(1L)
			.loginId("loginId")
			.nickname("nickname")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		UserPrincipal userPrincipal = new UserPrincipal(userDto);

		User user = User.builder()
			.id(userDto.getId())
			.loginId(userDto.getLoginId())
			.nickname(userDto.getNickname())
			.type(userDto.getType())
			.status(userDto.getStatus())
			.build();

		Address address = Address.builder()
			.alias("alias")
			.roadAddress("roadAddress")
			.detailAddress("detailAddress")
			.postalCode("postalCode")
			.build();
		ReflectionTestUtils.setField(address, "id", 1L);
		ReflectionTestUtils.setField(address, "user", user);

		doNothing().when(addressService).deleteAddress(user.getId(), address.getId());

		mockMvc.perform(delete("/api/addresses/{addressId}", 1L)
				.with(user(userPrincipal))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(addressService).deleteAddress(user.getId(), address.getId());
	}

	@Test
	@DisplayName("내 주소 삭제 실패 (401) - 로그인 안 된 사용자")
	void deleteAddress_fail_401() throws Exception {

		mockMvc.perform(delete("/api/addresses/{addressId}", 1L)
			.with(csrf()))
			.andExpect(status().isUnauthorized());

		verifyNoInteractions(addressService);
	}

	@Test
	@DisplayName("내 주소 삭제 실패 (404) - 존재하지 않는 주소")
	void deleteAddress_fail_404() throws Exception {

		UserDto userDto = UserDto.builder()
			.id(1L)
			.loginId("loginId")
			.nickname("nickname")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		UserPrincipal userPrincipal = new UserPrincipal(userDto);

		User user = User.builder()
			.id(userDto.getId())
			.loginId(userDto.getLoginId())
			.nickname(userDto.getNickname())
			.type(userDto.getType())
			.status(userDto.getStatus())
			.build();

		Long missingAddressId = 99L;

		doThrow(new AddressNotFoundException("수정할 주소를 찾을 수 없습니다.")).when(addressService).deleteAddress(user.getId(), missingAddressId);

		mockMvc.perform(delete("/api/addresses/{addressId}", missingAddressId)
				.with(user(userPrincipal))
				.with(csrf()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"));

		verify(addressService).deleteAddress(user.getId(), missingAddressId);
	}
}
