package shop.bluebooktle.backend.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.user.repository.MembershipLevelRepository;
import shop.bluebooktle.backend.user.service.impl.MembershipLevelServiceImpl;
import shop.bluebooktle.common.dto.membership.MembershipLevelDetailDto;

@ExtendWith(MockitoExtension.class)
class MembershipLevelServiceTest {

	@Mock
	private MembershipLevelRepository membershipLevelRepository;

	@InjectMocks
	private MembershipLevelServiceImpl membershipLevelService;

	@Test
	@DisplayName("전체 회원 등급 목록 조회 성공")
	void getAllMembershipLevels_success() {
		// given
		List<MembershipLevelDetailDto> mockLevels = List.of(
			new MembershipLevelDetailDto(1L, "일반", 1, new BigDecimal("0.00"), new BigDecimal("99999.99")),
			new MembershipLevelDetailDto(2L, "로얄", 2, new BigDecimal("100000.00"), new BigDecimal("199999.99")),
			new MembershipLevelDetailDto(3L, "골드", 3, new BigDecimal("200000.00"), new BigDecimal("299999.99")),
			new MembershipLevelDetailDto(4L, "플래티넘", 4, new BigDecimal("300000.00"), new BigDecimal("99999999.99"))
		);

		when(membershipLevelRepository.findAllMembershipLevels()).thenReturn(mockLevels);

		// when
		List<MembershipLevelDetailDto> result = membershipLevelService.getAllMembershipLevels();

		// then
		assertNotNull(result);
		assertEquals(4, result.size());

		// 각 등급의 핵심 정보 확인
		MembershipLevelDetailDto goldLevel = result.get(2);
		assertEquals("골드", goldLevel.name());
		assertEquals(3, goldLevel.rate()); // 적립률 3% 확인
		assertEquals(new BigDecimal("200000.00"), goldLevel.minNetSpent());

		MembershipLevelDetailDto platinumLevel = result.get(3);
		assertEquals("플래티넘", platinumLevel.name());
		assertEquals(4, platinumLevel.rate());

		// Repository 메소드가 정확히 1번 호출되었는지 검증
		verify(membershipLevelRepository, times(1)).findAllMembershipLevels();
	}

	@Test
	@DisplayName("전체 회원 등급 목록 조회 성공 - 결과가 없는 경우")
	void getAllMembershipLevels_success_emptyResult() {
		// given
		when(membershipLevelRepository.findAllMembershipLevels()).thenReturn(Collections.emptyList());

		// when
		List<MembershipLevelDetailDto> result = membershipLevelService.getAllMembershipLevels();

		// then
		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(membershipLevelRepository, times(1)).findAllMembershipLevels();
	}
}