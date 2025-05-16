package shop.bluebooktle.backend.order.service;

import static org.assertj.core.api.Assertions.*;
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

import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.repository.DeliveryRuleRepository;
import shop.bluebooktle.backend.order.service.impl.DeliveryRuleServiceImpl;
import shop.bluebooktle.common.exception.order.delivery_rule.CannotDeleteDefaultPolicyException;
import shop.bluebooktle.common.exception.order.delivery_rule.DefaultDeliveryRuleNotFoundException;
import shop.bluebooktle.common.exception.order.delivery_rule.DeliveryRuleAlreadyExistsException;

@ExtendWith(MockitoExtension.class)
class DeliveryRuleServiceTest {

	@InjectMocks
	private DeliveryRuleServiceImpl deliveryRuleService;

	@Mock
	private DeliveryRuleRepository deliveryRuleRepository;

	@Test
	@DisplayName("기본 배송 정책 조회 - 성공")
	void getDefaultRule_success() {
		DeliveryRule rule = DeliveryRule.builder()
			.name("기본 배송 정책")
			.price(BigDecimal.valueOf(30000))
			.deliveryFee(BigDecimal.ZERO)
			.build();

		given(deliveryRuleRepository.findById(1L)).willReturn(Optional.of(rule));

		DeliveryRule result = deliveryRuleService.getDefaultRule();

		assertThat(result).isEqualTo(rule);
	}

	@Test
	@DisplayName("기본 배송 정책 조회 - 실패")
	void getDefaultRule_fail() {
		given(deliveryRuleRepository.findById(1L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> deliveryRuleService.getDefaultRule())
			.isInstanceOf(DefaultDeliveryRuleNotFoundException.class);
	}

	@Test
	@DisplayName("새로운 배송 정책 생성 - 성공")
	void createPolicy_success() {
		String name = "새 정책";
		BigDecimal price = BigDecimal.valueOf(50000);
		BigDecimal fee = BigDecimal.valueOf(2500);

		given(deliveryRuleRepository.existsByName(name)).willReturn(false);
		given(deliveryRuleRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

		DeliveryRule result = deliveryRuleService.createPolicy(name, price, fee);

		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getPrice()).isEqualTo(price);
		assertThat(result.getDeliveryFee()).isEqualTo(fee);
	}

	@Test
	@DisplayName("기존 이름으로 배송 정책 생성 - 실패")
	void createPolicy_duplicateName_fail() {
		given(deliveryRuleRepository.existsByName("중복된이름")).willReturn(true);

		assertThatThrownBy(() -> deliveryRuleService.createPolicy("중복된이름", BigDecimal.TEN, BigDecimal.ONE))
			.isInstanceOf(DeliveryRuleAlreadyExistsException.class);
	}

	@Test
	@DisplayName("배송 정책 삭제 - 성공")
	void deletePolicy_success() {
		Long id = 2L;
		DeliveryRule rule = DeliveryRule.builder().name("삭제 가능 정책").build();
		given(deliveryRuleRepository.findById(id)).willReturn(Optional.of(rule));

		deliveryRuleService.deletePolicy(id);

		verify(deliveryRuleRepository).delete(rule);
	}

	@Test
	@DisplayName("기본 배송 정책 삭제 시도 - 실패")
	void deleteDefaultPolicy_fail() {
		Long id = 1L;
		DeliveryRule rule = DeliveryRule.builder().name("기본 배송 정책").build();
		given(deliveryRuleRepository.findById(id)).willReturn(Optional.of(rule));

		assertThatThrownBy(() -> deliveryRuleService.deletePolicy(id))
			.isInstanceOf(CannotDeleteDefaultPolicyException.class);
	}

	@Test
	@DisplayName("배송 정책 단건 조회 - 성공")
	void getRule_success() {
		Long id = 3L;
		DeliveryRule rule = DeliveryRule.builder().name("프리미엄 배송").build();
		given(deliveryRuleRepository.findById(id)).willReturn(Optional.of(rule));

		DeliveryRule result = deliveryRuleService.getRule(id);

		assertThat(result.getName()).isEqualTo("프리미엄 배송");
	}

	@Test
	@DisplayName("배송 정책 전체 조회 - 성공")
	void getAll_success() {
		List<DeliveryRule> rules = List.of(
			DeliveryRule.builder().name("A").build(),
			DeliveryRule.builder().name("B").build()
		);
		given(deliveryRuleRepository.findAll()).willReturn(rules);

		List<DeliveryRule> result = deliveryRuleService.getAll();

		assertThat(result).hasSize(2);
	}
}
