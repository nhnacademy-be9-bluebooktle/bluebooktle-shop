package shop.bluebooktle.backend.book_order.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.book_order.jpa.PackagingOptionRepository;
import shop.bluebooktle.backend.book_order.service.impl.PackagingOptionServiceImpl;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionRequest;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.common.exception.book_order.PackagingOptionAlreadyExistsException;
import shop.bluebooktle.common.exception.book_order.PackagingOptionNotFoundException;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PackagingOptionServiceTest {

	@InjectMocks
	private PackagingOptionServiceImpl service;

	@Mock
	private PackagingOptionRepository packagingOptionRepository;

	@Test
	@DisplayName("포장 옵션 등록 - 성공")
	void createPackagingOption_success() {
		// given
		PackagingOptionRequest req = PackagingOptionRequest.builder()
			.name("땡땡이 포장지")
			.price(BigDecimal.valueOf(500))
			.build();

		given(packagingOptionRepository.existsByName("땡땡이 포장지"))
			.willReturn(false);

		PackagingOption saved = PackagingOption.builder()
			.name("땡땡이 포장지")
			.price(BigDecimal.valueOf(500))
			.build();
		// id 및 createdAt 등 가짜값 주입
		org.springframework.test.util.ReflectionTestUtils.setField(saved, "id", 1L);

		given(packagingOptionRepository.save(any(PackagingOption.class)))
			.willReturn(saved);

		// when
		PackagingOptionInfoResponse resp = service.createPackagingOption(req);

		// then
		assertThat(resp.getId()).isEqualTo(1L);
		assertThat(resp.getName()).isEqualTo("땡땡이 포장지");
		assertThat(resp.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(500));
		then(packagingOptionRepository).should().save(any(PackagingOption.class));
	}

	@Test
	@DisplayName("포장 옵션 등록 - 중복 예외")
	void createPackagingOption_duplicateName() {
		// given
		PackagingOptionRequest req = PackagingOptionRequest.builder()
			.name("중복 포장지")
			.price(BigDecimal.valueOf(100))
			.build();

		given(packagingOptionRepository.existsByName("중복 포장지"))
			.willReturn(true);

		// when & then
		assertThatThrownBy(() -> service.createPackagingOption(req))
			.isInstanceOf(PackagingOptionAlreadyExistsException.class);

		then(packagingOptionRepository).should(never()).save(any());
	}

	@Test
	@DisplayName("단건 조회 - 성공")
	void getPackagingOption_success() {
		// given
		PackagingOption option = PackagingOption.builder()
			.name("체크 포장지")
			.price(BigDecimal.valueOf(200))
			.build();
		ReflectionTestUtils.setField(option, "id", 2L);

		given(packagingOptionRepository.findById(2L))
			.willReturn(Optional.of(option));

		// when
		PackagingOptionInfoResponse resp = service.getPackagingOption(2L);

		// then
		assertThat(resp.getId()).isEqualTo(2L);
		assertThat(resp.getName()).isEqualTo("체크 포장지");
		assertThat(resp.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(200));
	}

	@Test
	@DisplayName("단건 조회 - 실패")
	void getPackagingOption_notFound() {
		// given
		given(packagingOptionRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> service.getPackagingOption(99L))
			.isInstanceOf(PackagingOptionNotFoundException.class);
	}

	@Test
	@DisplayName("전체 조회 - 성공")
	void getPackagingOptions_success() {
		// given
		PackagingOption o1 = PackagingOption.builder()
			.name("A")
			.price(BigDecimal.valueOf(100))
			.build();
		PackagingOption o2 = PackagingOption.builder()
			.name("B")
			.price(BigDecimal.valueOf(200))
			.build();
		ReflectionTestUtils.setField(o1, "id", 10L);
		ReflectionTestUtils.setField(o2, "id", 11L);

		List<PackagingOption> list = List.of(o1, o2);
		Pageable pageable = PageRequest.of(0, 10);
		Page<PackagingOption> page = new PageImpl<>(list, pageable, list.size());

		given(packagingOptionRepository.findAll(pageable))
			.willReturn(page);

		// when
		Page<PackagingOptionInfoResponse> result = service.getPackagingOptions(pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent())
			.extracting(PackagingOptionInfoResponse::getId)
			.containsExactlyInAnyOrder(10L, 11L);
	}

	@Test
	@DisplayName("검색 조회 - 성공")
	void searchPackagingOption_success() {
		// given
		PackagingOption o = PackagingOption.builder()
			.name("무지개 도트 포장지")
			.price(BigDecimal.valueOf(300))
			.build();
		ReflectionTestUtils.setField(o, "id", 20L);

		List<PackagingOption> list = List.of(o);
		Pageable pageable = PageRequest.of(0, 5);
		Page<PackagingOption> page = new PageImpl<>(list, pageable, list.size());

		given(packagingOptionRepository.searchNameContaining("무지개", pageable))
			.willReturn(page);

		// when
		Page<PackagingOptionInfoResponse> result = service.searchPackagingOption("무지개", pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getId()).isEqualTo(20L);
	}

	@Test
	@DisplayName("수정 - 성공")
	void updatePackagingOption_success() {
		// given
		PackagingOption o = PackagingOption.builder()
			.name("옛날 포장지")
			.price(BigDecimal.valueOf(100))
			.build();
		ReflectionTestUtils.setField(o, "id", 30L);

		given(packagingOptionRepository.findById(30L))
			.willReturn(Optional.of(o));

		PackagingOptionRequest req = PackagingOptionRequest.builder()
			.name("새로운 포장지")
			.price(BigDecimal.valueOf(150))
			.build();

		// when
		PackagingOptionInfoResponse resp = service.updatePackagingOption(30L, req);

		// then
		assertThat(resp.getName()).isEqualTo("새로운 포장지");
		assertThat(resp.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(150));
	}

	@Test
	@DisplayName("수정 - 실패(존재하지 않음)")
	void updatePackagingOption_notFound() {
		// given
		given(packagingOptionRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		PackagingOptionRequest req = PackagingOptionRequest.builder()
			.name("짱구 포장지")
			.price(BigDecimal.ONE)
			.build();

		// when & then
		assertThatThrownBy(() -> service.updatePackagingOption(999L, req))
			.isInstanceOf(PackagingOptionNotFoundException.class);
	}

	@Test
	@DisplayName("삭제 - 성공")
	void deletePackagingOption_success() {
		// given
		PackagingOption o = PackagingOption.builder().build();
		ReflectionTestUtils.setField(o, "id", 40L);
		given(packagingOptionRepository.findById(40L)).willReturn(Optional.of(o));

		// when
		service.deletePackagingOption(40L);

		// then
		then(packagingOptionRepository).should().delete(o);
	}

	@Test
	@DisplayName("삭제 - 실패(존재하지 않음)")
	void deletePackagingOption_notFound() {
		// given
		given(packagingOptionRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> service.deletePackagingOption(123L))
			.isInstanceOf(PackagingOptionNotFoundException.class);
	}
}
