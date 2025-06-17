package shop.bluebooktle.backend.point.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.point.repository.PointPolicyRepository;
import shop.bluebooktle.backend.point.repository.PointSourceTypeRepository;
import shop.bluebooktle.backend.point.service.impl.PointPolicyServiceImpl;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.domain.point.PolicyType;
import shop.bluebooktle.common.dto.point.request.PointPolicyCreateRequest;
import shop.bluebooktle.common.dto.point.request.PointPolicyUpdateRequest;
import shop.bluebooktle.common.dto.point.response.PointPolicyResponse;
import shop.bluebooktle.common.dto.point.response.PointRuleResponse;
import shop.bluebooktle.common.entity.point.PointPolicy;
import shop.bluebooktle.common.entity.point.PointSourceType;
import shop.bluebooktle.common.exception.point.PointPolicyNotFoundException;
import shop.bluebooktle.common.exception.point.PointSourceNotFountException;

@ExtendWith(MockitoExtension.class)
class PointPolicyServiceTest {

	@InjectMocks
	private PointPolicyServiceImpl service;

	@Mock
	private PointPolicyRepository pointPolicyRepository;

	@Mock
	private PointSourceTypeRepository pointSourceTypeRepository;

	@Test
	@DisplayName("포인트 정책 생성 - 성공")
	void create_success() {
		PointPolicyCreateRequest request = new PointPolicyCreateRequest(1L, PolicyType.AMOUNT, BigDecimal.valueOf(100));
		PointSourceType pst = mock(PointSourceType.class);

		PointPolicy savedPolicy = mock(PointPolicy.class);
		given(pointSourceTypeRepository.findById(anyLong())).willReturn(Optional.of(pst));

		given(pointPolicyRepository.save(any())).willReturn(savedPolicy);
		given(savedPolicy.getId()).willReturn(1L);

		Long createdId = service.create(request);

		assertThat(createdId).isEqualTo(1L);
	}

	@Test
	@DisplayName("포인트 정책 생성 실패 - 포인트 출처 없음")
	void create_sourceTypeNotFound_fail() {
		PointPolicyCreateRequest request = new PointPolicyCreateRequest(99L, PolicyType.PERCENTAGE, BigDecimal.ZERO);
		given(pointSourceTypeRepository.findById(request.pointSourceTypeId())).willReturn(Optional.empty());

		assertThrows(PointSourceNotFountException.class, () -> service.create(request));
	}

	@Test
	@DisplayName("포인트 정책 수정 - 성공")
	void update_success() {
		PointPolicyUpdateRequest request = new PointPolicyUpdateRequest(1L, PolicyType.PERCENTAGE,
			BigDecimal.valueOf(5), false);

		PointPolicy mockPolicy = mock(PointPolicy.class);
		given(pointPolicyRepository.findById(request.pointPolicyId())).willReturn(Optional.of(mockPolicy));

		service.update(request);

		verify(mockPolicy).changePolicyType(request.policyType());
		verify(mockPolicy).changeValue(request.value());
		verify(mockPolicy).changeIsActive(request.isActive());

		verify(pointPolicyRepository).save(mockPolicy);
	}

	@Test
	@DisplayName("포인트 정책 수정 - 변경 사항이 없을 경우 성공")
	void update_success_noChanges() {

		PointPolicyUpdateRequest request = new PointPolicyUpdateRequest(1L, null, null, null);

		PointPolicy mockPolicy = mock(PointPolicy.class);
		given(pointPolicyRepository.findById(request.pointPolicyId())).willReturn(Optional.of(mockPolicy));

		service.update(request);

		verify(mockPolicy, never()).changePolicyType(request.policyType());
		verify(mockPolicy, never()).changeValue(request.value());
		verify(mockPolicy, never()).changeIsActive(request.isActive());
		verify(pointPolicyRepository).save(mockPolicy);
	}

	@Test
	@DisplayName("포인트 정책 삭제 - 성공")
	void delete_success() {

		PointPolicy policy = mock(PointPolicy.class);
		given(pointPolicyRepository.findById(anyLong())).willReturn(Optional.of(policy));

		doNothing().when(pointPolicyRepository).delete(policy);

		service.delete(anyLong());

		verify(pointPolicyRepository).delete(policy);
	}

	@Test
	@DisplayName("포인트 정책 삭제 -  NotFound 실패")
	void delete_fail() {
		given(pointPolicyRepository.findById(anyLong())).willReturn(Optional.empty());
		assertThrows(PointPolicyNotFoundException.class, () -> service.delete(anyLong()));
	}

	@Test
	@DisplayName("포인트 단건 조회 - 성공")
	void get_success() {
		PointPolicy policy = mock(PointPolicy.class);
		given(policy.getId()).willReturn(1L);
		given(policy.getPolicyType()).willReturn(PolicyType.AMOUNT);
		given(policy.getValue()).willReturn(BigDecimal.valueOf(100));

		given(pointPolicyRepository.findById(anyLong())).willReturn(Optional.of(policy));
		PointPolicyResponse response = service.get(anyLong());
		verify(pointPolicyRepository).findById(anyLong());

		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(policy.getId());
		assertThat(response.policyType()).isEqualTo(policy.getPolicyType());
		assertThat(response.value()).isEqualTo(policy.getValue());
	}

	@Test
	@DisplayName("포인트 단건 조회 - 실패")
	void get_fail() {
		given(pointPolicyRepository.findById(anyLong())).willReturn(Optional.empty());
		assertThrows(PointPolicyNotFoundException.class, () -> service.get(anyLong()));
	}

	@Test
	@DisplayName("Enum으로 포인트 규칙 조회 - 성공")
	void getRuleBySourceTypeEnum_success() {
		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.PAYMENT_EARN;
		PointSourceType sourceType = mock(PointSourceType.class);
		given(sourceType.getId()).willReturn(sourceTypeEnum.getId());
		given(sourceType.getSourceType()).willReturn(sourceTypeEnum.getSourceType());

		PointPolicy policy = mock(PointPolicy.class);
		given(policy.getId()).willReturn(10L);
		given(policy.getPolicyType()).willReturn(PolicyType.PERCENTAGE);
		given(policy.getValue()).willReturn(BigDecimal.ONE);
		given(policy.getIsActive()).willReturn(true);

		given(pointSourceTypeRepository.findById(anyLong())).willReturn(Optional.of(sourceType));
		given(pointPolicyRepository.findByPointSourceType(any(PointSourceType.class))).willReturn(Optional.of(policy));

		PointRuleResponse response = service.getRuleBySourceTypeEnum(sourceTypeEnum);

		assertThat(response.pointPolicyId()).isEqualTo(10L);
		assertThat(response.pointSourceTypeId()).isEqualTo(sourceTypeEnum.getId());
		assertThat(response.sourceType()).isEqualTo(sourceTypeEnum.getSourceType());
		assertThat(response.policyType()).isEqualTo(PolicyType.PERCENTAGE);
		assertThat(response.isActive()).isTrue();
	}

	@Test
	@DisplayName("Enum으로 포인트 규칙 조회 실패 - 정책 없음")
	void getRuleBySourceTypeEnum_policyNotFound_fail() {
		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.PAYMENT_EARN;
		PointSourceType sourceType = mock(PointSourceType.class);
		given(pointSourceTypeRepository.findById(anyLong())).willReturn(Optional.of(sourceType));
		given(pointPolicyRepository.findByPointSourceType(sourceType)).willReturn(Optional.empty());
		assertThrows(PointPolicyNotFoundException.class, () -> service.getRuleBySourceTypeEnum(sourceTypeEnum));
	}

	@Test
	@DisplayName("모든 포인트 정책 조회")
	void findAll_success() {
		PointPolicy policy1 = mock(PointPolicy.class);
		PointPolicy policy2 = mock(PointPolicy.class);

		given(pointPolicyRepository.findAll()).willReturn(List.of(policy1, policy2));

		List<PointPolicyResponse> responses = service.findAll();

		assertThat(responses).hasSize(2);
	}

	@Test
	@DisplayName("모든 포인트 규칙 조회 ")
	void getAll() {
		PointSourceType pst1 = mock(PointSourceType.class);
		given(pst1.getId()).willReturn(1L);
		given(pst1.getSourceType()).willReturn("pst1");

		PointSourceType pst2 = mock(PointSourceType.class);
		given(pst2.getId()).willReturn(2L);
		given(pst2.getSourceType()).willReturn("pst2");

		PointPolicy policy1 = mock(PointPolicy.class);
		given(policy1.getId()).willReturn(1L);
		given(policy1.getPolicyType()).willReturn(PolicyType.PERCENTAGE);
		given(policy1.getValue()).willReturn(BigDecimal.ONE);
		given(policy1.getPointSourceType()).willReturn(pst1);
		given(policy1.getIsActive()).willReturn(true);

		PointPolicy policy2 = mock(PointPolicy.class);
		given(policy2.getId()).willReturn(2L);
		given(policy2.getPolicyType()).willReturn(PolicyType.PERCENTAGE);
		given(policy2.getValue()).willReturn(BigDecimal.ONE);
		given(policy2.getPointSourceType()).willReturn(pst2);
		given(policy2.getIsActive()).willReturn(true);

		given(pointPolicyRepository.findAll()).willReturn(List.of(policy1, policy2));

		List<PointRuleResponse> responses = service.getAll();

		assertThat(responses).hasSize(2);
	}
}