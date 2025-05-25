package shop.bluebooktle.backend.order.service;

import static org.assertj.core.api.Assertions.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.repository.DeliveryRuleRepository;
import shop.bluebooktle.backend.order.service.impl.DeliveryRuleServiceImpl;
import shop.bluebooktle.common.domain.order.Region;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleCreateRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.common.exception.order.delivery_rule.CannotDeleteDefaultPolicyException;
import shop.bluebooktle.common.exception.order.delivery_rule.DefaultDeliveryRuleNotFoundException;
import shop.bluebooktle.common.exception.order.delivery_rule.DeliveryRuleAlreadyExistsException;
import shop.bluebooktle.common.exception.order.delivery_rule.DeliveryRuleNotFoundException;

@ExtendWith(MockitoExtension.class)
class DeliveryRuleServiceTest {

	@InjectMocks
	private DeliveryRuleServiceImpl service;

	@Mock
	private DeliveryRuleRepository repository;

	@Test
	@DisplayName("기본 정책 조회 - 성공")
	void getDefaultRule_success() {
		DeliveryRule entity = DeliveryRule.builder()
			.ruleName("기본 배송 정책")
			.deliveryFee(new BigDecimal("2500"))
			.freeDeliveryThreshold(new BigDecimal("50000"))
			.region(Region.ALL)
			.isActive(true)
			.build();
		// 구현체는 findByRegion(...)을 호출합니다.
		given(repository.findByRegion(Region.ALL))
			.willReturn(Optional.of(entity));

		DeliveryRuleResponse result = service.getDefaultRule();

		assertThat(result.ruleName()).isEqualTo(entity.getRuleName());
		assertThat(result.deliveryFee()).isEqualByComparingTo(entity.getDeliveryFee());
		assertThat(result.freeDeliveryThreshold()).isEqualByComparingTo(entity.getFreeDeliveryThreshold());
	}

	@Test
	@DisplayName("기본 정책 조회 - 실패")
	void getDefaultRule_fail() {
		given(repository.findByRegion(Region.ALL))
			.willReturn(Optional.empty());

		assertThatThrownBy(() -> service.getDefaultRule())
			.isInstanceOf(DefaultDeliveryRuleNotFoundException.class);
	}

	@Test
	@DisplayName("배송 정책 생성 - 성공")
	void createRule_success() {
		DeliveryRuleCreateRequest req = new DeliveryRuleCreateRequest(
			"제주 추가비용",
			new BigDecimal("3000"),
			new BigDecimal("10000"),
			Region.JEJU,
			true
		);
		given(repository.existsByRuleName(req.ruleName())).willReturn(false);
		// JEJU는 기본 정책(ALL)이 아니므로 추가 중복 검사는 생략됩니다.

		DeliveryRule saved = DeliveryRule.builder()
			.ruleName(req.ruleName())
			.deliveryFee(req.deliveryFee())
			.freeDeliveryThreshold(req.freeDeliveryThreshold())
			.region(req.region())
			.isActive(true)
			.build();
		saved.setId(10L);
		given(repository.save(any(DeliveryRule.class))).willReturn(saved);

		Long id = service.createRule(req);

		assertThat(id).isEqualTo(10L);
	}

	@Test
	@DisplayName("배송 정책 생성 - 중복 이름 실패")
	void createRule_duplicateName_fail() {
		DeliveryRuleCreateRequest req = new DeliveryRuleCreateRequest(
			"중복",
			BigDecimal.ZERO,
			BigDecimal.ZERO,
			Region.ALL,
			true
		);
		given(repository.existsByRuleName(req.ruleName())).willReturn(true);

		assertThatThrownBy(() -> service.createRule(req))
			.isInstanceOf(DeliveryRuleAlreadyExistsException.class);
	}

	@Test
	@DisplayName("기본 정책 중복 생성 실패")
	void createRule_duplicateDefault_fail() {
		DeliveryRuleCreateRequest req = new DeliveryRuleCreateRequest(
			"기본",
			BigDecimal.ZERO,
			BigDecimal.ZERO,
			Region.ALL,
			true
		);
		given(repository.existsByRuleName(req.ruleName())).willReturn(false);
		// 기본 정책 중복 검사용
		given(repository.findByRegionAndIsActiveTrue(Region.ALL))
			.willReturn(Optional.of(
				DeliveryRule.builder()
					.ruleName("기존 기본")
					.deliveryFee(BigDecimal.ZERO)
					.freeDeliveryThreshold(BigDecimal.ZERO)
					.region(Region.ALL)
					.isActive(true)
					.build()
			));

		assertThatThrownBy(() -> service.createRule(req))
			.isInstanceOf(DeliveryRuleAlreadyExistsException.class);
	}

	@Test
	@DisplayName("단건 조회 - 성공")
	void getRule_success() {
		DeliveryRule entity = DeliveryRule.builder()
			.ruleName("프리미엄")
			.deliveryFee(BigDecimal.ONE)
			.freeDeliveryThreshold(BigDecimal.TEN)
			.region(Region.MOUNTAINOUS_AREA)
			.isActive(true)
			.build();
		given(repository.findById(5L)).willReturn(Optional.of(entity));

		DeliveryRuleResponse dto = service.getRule(5L);

		assertThat(dto.ruleName()).isEqualTo(entity.getRuleName());
	}

	@Test
	@DisplayName("단건 조회 - 실패")
	void getRule_notFound_fail() {
		given(repository.findById(99L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> service.getRule(99L))
			.isInstanceOf(DeliveryRuleNotFoundException.class);
	}

	@Test
	@DisplayName("전체 조회 - 성공")
	void getAll_success() {
		DeliveryRule a = DeliveryRule.builder()
			.ruleName("A").deliveryFee(BigDecimal.ONE)
			.freeDeliveryThreshold(BigDecimal.ZERO).region(Region.ALL).isActive(true)
			.build();
		DeliveryRule b = DeliveryRule.builder()
			.ruleName("B").deliveryFee(BigDecimal.TEN)
			.freeDeliveryThreshold(BigDecimal.ZERO).region(Region.ALL).isActive(true)
			.build();
		Page<DeliveryRule> page = new PageImpl<>(List.of(a, b));
		Pageable pageable = Pageable.unpaged();
		given(repository.findAll(pageable)).willReturn(page);

		Page<DeliveryRuleResponse> result = service.getAll(pageable);

		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).extracting(DeliveryRuleResponse::ruleName)
			.containsExactly("A", "B");
	}

	@Test
	@DisplayName("삭제 - 성공")
	void deleteRule_success() {
		DeliveryRule entity = DeliveryRule.builder()
			.region(Region.JEJU).isActive(true).build();
		given(repository.findById(7L)).willReturn(Optional.of(entity));

		service.deleteRule(7L);

		then(repository).should().delete(entity);
	}

	@Test
	@DisplayName("기본 정책 삭제 시도 - 실패")
	void deleteDefaultRule_fail() {
		DeliveryRule entity = DeliveryRule.builder()
			.region(Region.ALL).isActive(true).build();
		given(repository.findById(1L)).willReturn(Optional.of(entity));

		assertThatThrownBy(() -> service.deleteRule(1L))
			.isInstanceOf(CannotDeleteDefaultPolicyException.class);
	}
}
