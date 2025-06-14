package shop.bluebooktle.backend.order.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.order.service.RefundService;
import shop.bluebooktle.common.domain.refund.RefundReason;
import shop.bluebooktle.common.domain.refund.RefundSearchType;
import shop.bluebooktle.common.domain.refund.RefundStatus;
import shop.bluebooktle.common.dto.refund.request.RefundSearchRequest;
import shop.bluebooktle.common.dto.refund.request.RefundUpdateRequest;
import shop.bluebooktle.common.dto.refund.response.AdminRefundDetailResponse;
import shop.bluebooktle.common.dto.refund.response.RefundListResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@ActiveProfiles("test")
@WebMvcTest(controllers = AdminRefundController.class)
public class AdminRefundControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RefundService refundService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("관리자 환불 내역 조회 - 성공")
	@WithMockUser(roles = "ADMIN")
	void getRefundList_success() throws Exception {
		RefundListResponse refund1 = new RefundListResponse(
			1L,
			10L,
			"ORD-123",
			"홍길동",
			LocalDateTime.now(),
			new BigDecimal("10000"),
			RefundReason.CHANGE_OF_MIND,
			RefundStatus.PENDING // status
		);
		Page<RefundListResponse> page = new PageImpl<>(List.of(refund1), PageRequest.of(0, 10), 1);
		given(refundService.getRefundList(any(RefundSearchRequest.class), any(Pageable.class)))
			.willReturn(page);

		mockMvc.perform(get("/api/admin/refunds")
				.param("page", "0")
				.param("size", "10")
				.param("status", "PENDING")
				.param("searchType", RefundSearchType.ORDER_KEY.name())
				.param("keyword", "ORD-123")
				.param("startDate", LocalDate.now().minusDays(7).toString())
				.param("endDate", LocalDate.now().toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content[0].orderKey").value("ORD-123"))
			.andExpect(jsonPath("$.data.content[0].ordererName").value("홍길동"));

		verify(refundService).getRefundList(any(RefundSearchRequest.class), any(Pageable.class));
	}

	@Test
	@DisplayName("관리자 환불 상세 조회 - 성공")
	@WithMockUser(roles = "ADMIN")
	void getRefundDetail_success() throws Exception {
		Long refundId = 1L;
		AdminRefundDetailResponse detailResponse = new AdminRefundDetailResponse(
			refundId,
			LocalDateTime.now(),
			RefundStatus.PENDING,
			new BigDecimal("10000"),
			RefundReason.CHANGE_OF_MIND,
			"단순 변심으로 인한 환불",
			10L,
			"ORD-123",
			"홍길동",
			"testuser"
		);
		given(refundService.getAdminRefundDetail(refundId)).willReturn(detailResponse);

		mockMvc.perform(get("/api/admin/refunds/{refundId}", refundId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.refundId").value(refundId))
			.andExpect(jsonPath("$.data.orderKey").value("ORD-123"))
			.andExpect(jsonPath("$.data.userLoginId").value("testuser"));

		verify(refundService).getAdminRefundDetail(refundId);
	}

	@Test
	@DisplayName("관리자 환불 처리 - 성공")
	@WithMockUser(roles = "ADMIN")
	void updateRefundStatus_success() throws Exception {
		RefundUpdateRequest request = new RefundUpdateRequest(
			1L,
			RefundStatus.COMPLETE
		);

		doNothing().when(refundService).updateRefund(any(RefundUpdateRequest.class));

		mockMvc.perform(post("/api/admin/refunds/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(refundService).updateRefund(any(RefundUpdateRequest.class));
	}
}