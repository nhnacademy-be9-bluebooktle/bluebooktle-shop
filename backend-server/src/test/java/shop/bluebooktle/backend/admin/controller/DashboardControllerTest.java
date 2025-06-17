package shop.bluebooktle.backend.admin.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.admin.service.DashboardService;
import shop.bluebooktle.common.dto.admin.DashboardStatusResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(DashboardController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private DashboardService dashboardService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("관리자 대시보드 상태 조회 성공")
	@WithMockUser(username = "admin", roles = "ADMIN")
	void getDashboardStatus_success() throws Exception {
		// given
		DashboardStatusResponse response = new DashboardStatusResponse(100L, 5L, 2L);
		when(dashboardService.getDashboardStatus()).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/admin/dashboard/status"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.totalUserCount").value(100))
			.andExpect(jsonPath("$.data.todayOrderCount").value(5))
			.andExpect(jsonPath("$.data.pendingOrderCount").value(2));

		verify(dashboardService, times(1)).getDashboardStatus();
	}
}
