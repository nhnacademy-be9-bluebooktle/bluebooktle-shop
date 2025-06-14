package shop.bluebooktle.backend.order.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.backend.order.dto.response.OrderStateResponse;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.backend.order.service.OrderStateService;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@ActiveProfiles("test")
@WebMvcTest(controllers = OrderStateController.class)
public class OrderStateControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderStateService orderStateService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("주문 상태 전체 조회 - 성공")
	@WithMockUser
	void getAllStatus_success() throws Exception {
		OrderState pending = OrderState.builder().state(OrderStatus.PENDING).build();
		OrderState completed = OrderState.builder().state(OrderStatus.COMPLETED).build();

		given(orderStateService.getAll()).willReturn(List.of(pending, completed));

		mockMvc.perform(get("/api/admin/order-status/all"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray());

		verify(orderStateService).getAll();
	}

	@Test
	@DisplayName("주문 상태 단일 조회 - 성공")
	@WithMockUser
	void getStatus_success() throws Exception {
		OrderStatus status = OrderStatus.SHIPPING;

		OrderStateResponse response = new OrderStateResponse(1L, status);

		given(orderStateService.getByState(status)).willReturn(Optional.of(response));

		mockMvc.perform(get("/api/admin/order-status")
				.param("name", status.name())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.state").value(status.name()));

		verify(orderStateService).getByState(status);
	}

	@Test
	@DisplayName("주문 상태 단일 조회 - 실패 (존재하지 않는 상태)")
	@WithMockUser
	void getStatus_notFound() throws Exception {
		OrderStatus status = OrderStatus.RETURNED;

		given(orderStateService.getByState(status)).willReturn(Optional.empty());

		mockMvc.perform(get("/api/admin/order-status")
				.param("name", status.name())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		verify(orderStateService).getByState(status);
	}
}
