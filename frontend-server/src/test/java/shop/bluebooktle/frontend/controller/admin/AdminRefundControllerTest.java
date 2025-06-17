package shop.bluebooktle.frontend.controller.admin;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.common.domain.refund.RefundReason;
import shop.bluebooktle.common.domain.refund.RefundSearchType;
import shop.bluebooktle.common.domain.refund.RefundStatus;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.refund.response.AdminRefundDetailResponse;
import shop.bluebooktle.frontend.config.advice.GlobalCartCountAdvice;
import shop.bluebooktle.frontend.config.advice.GlobalCategoryInfoAdvice;
import shop.bluebooktle.frontend.config.advice.GlobalUserInfoAdvice;
import shop.bluebooktle.frontend.service.AdminRefundService;

@WebMvcTest(
	controllers = AdminRefundController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalUserInfoAdvice.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalCartCountAdvice.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalCategoryInfoAdvice.class)
	}
)
@TestPropertySource(properties = {
	"server.gateway-url=http://localhost:8080",
	"minio.endpoint=http://localhost:9000", // 더미값
	"toss.client-key=testClientKey"
})
public class AdminRefundControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminRefundService adminRefundService;

	@Test
	@DisplayName("GET /admin/refunds — 리스트 정상")
	void listRefunds() throws Exception {
		// RefundSearchRequest, Pageable에 상관없이 빈 페이지를 돌려주도록 stub
		given(adminRefundService.getRefunds(any(), any()))
			.willReturn(new PaginationData<>(
				new PageImpl<>(List.of(), PageRequest.of(0, 10), 0)
			));

		mockMvc.perform(get("/admin/refunds"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/refund/refund_list"))
			.andExpect(model().attribute("pageTitle", "환불 관리"))
			.andExpect(model().attributeExists("currentURI"))
			.andExpect(model().attributeExists("paginationData"))
			.andExpect(model().attributeExists("searchRequest"))
			.andExpect(model().attribute("refundStatusOptions", RefundStatus.values()))
			.andExpect(model().attribute("searchTypeOptions", RefundSearchType.values()));
	}

	@Test
	@DisplayName("GET /admin/refunds/{id} — 상세 정상")
	void refundDetail_success() throws Exception {
		AdminRefundDetailResponse dummy = new AdminRefundDetailResponse(
			7L,                                        // refundId
			LocalDateTime.of(2025,1,2,15,30),          // requestDate
			RefundStatus.PENDING,                    // status
			BigDecimal.valueOf(5000),                  // refundAmount
			RefundReason.CHANGE_OF_MIND,               // reason (enum)
			"사용자 변심",                             // reasonDetail
			123L,                                      // orderId
			"ORD123",                                  // orderKey
			"홍길동",                                  // ordererName
			"user1"                                    // userLoginId
		);
		given(adminRefundService.getRefundDetail(7L)).willReturn(dummy);

		mockMvc.perform(get("/admin/refunds/7"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/refund/refund_detail"))
			.andExpect(model().attribute("pageTitle", "환불 상세 (ID: 7)"))
			.andExpect(model().attributeExists("currentURI"))
			.andExpect(model().attribute("refund", dummy))
			.andExpect(model().attribute("nextStatusOptions", RefundStatus.values()))
			.andExpect(model().attributeExists("updateRequest"));
	}

	@Test
	@DisplayName("GET /admin/refunds/{id} — 상세 예외 → 리스트로 리다이렉트")
	void refundDetail_exception() throws Exception {
		given(adminRefundService.getRefundDetail(5L)).willThrow(new RuntimeException("err"));
		mockMvc.perform(get("/admin/refunds/5"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/refunds"))
			.andExpect(flash().attribute("globalErrorMessage", "환불 정보를 불러오는 데 실패했습니다."));
	}

	@Test
	@DisplayName("POST /admin/refunds/update-status — 성공")
	void updateRefundStatus_success() throws Exception {
		mockMvc.perform(post("/admin/refunds/update-status")
				.param("refundId", "3")
				.param("status", RefundStatus.COMPLETE.name())
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/refunds/3"))
			.andExpect(flash().attribute("globalSuccessMessage",
				"환불(ID: 3) 상태가 성공적으로 변경되었습니다."));
		then(adminRefundService).should().updateRefund(any());
	}


	@Test
	@DisplayName("POST /admin/refunds/update-status — 예외 → 상세 페이지로 리다이렉트")
	void updateRefundStatus_exception() throws Exception {
		// updateRefund 호출 시 RuntimeException 발생하도록 stub
		willThrow(new RuntimeException("oops"))
			.given(adminRefundService).updateRefund(any());

		mockMvc.perform(post("/admin/refunds/update-status")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("refundId", "4")
				.param("status", RefundStatus.REJECTED.name())
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/refunds/4"))
			// 에러 메시지 플래시에만 담김
			.andExpect(flash().attribute("globalErrorMessage",
				"상태 변경 중 오류가 발생했습니다: oops"))
			.andExpect(flash().attribute("globalSuccessMessage", nullValue()));
	}
}
