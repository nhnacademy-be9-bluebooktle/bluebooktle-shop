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

import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.book_order.repository.PackagingOptionRepository;
import shop.bluebooktle.backend.book_order.service.impl.PackagingOptionServiceImpl;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionRequest;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.common.exception.book_order.PackagingOptionAlreadyExistsException;
import shop.bluebooktle.common.exception.book_order.PackagingOptionNotFoundException;

@ExtendWith(MockitoExtension.class)
class PackagingOptionServiceImplTest {

	@InjectMocks
	private PackagingOptionServiceImpl service;

	@Mock
	private PackagingOptionRepository packagingOptionRepository;

	@Test
	@DisplayName("포장 옵션 등록 - 성공")
	void createPackagingOption_success() {
		// given
		PackagingOptionRequest req = PackagingOptionRequest.builder()
			.name("Gift Wrap")
			.price(BigDecimal.valueOf(1000))
			.build();
		given(packagingOptionRepository.existsByName("Gift Wrap")).willReturn(false);

		PackagingOption saved = PackagingOption.builder()
			.name("Gift Wrap")
			.price(BigDecimal.valueOf(1000))
			.build();
		ReflectionTestUtils.setField(saved, "id", 1L);
		given(packagingOptionRepository.save(any())).willReturn(saved);

		// when
		PackagingOptionInfoResponse resp = service.createPackagingOption(req);

		// then
		assertThat(resp.getId()).isEqualTo(1L);
		assertThat(resp.getName()).isEqualTo("Gift Wrap");
		assertThat(resp.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(1000));
	}

	@Test
	@DisplayName("포장 옵션 등록 - 중복 예외")
	void createPackagingOption_duplicate() {
		// given
		PackagingOptionRequest req = PackagingOptionRequest.builder()
			.name("Wrap")
			.price(BigDecimal.ZERO)
			.build();
		given(packagingOptionRepository.existsByName("Wrap")).willReturn(true);

		// when & then
		assertThatThrownBy(() -> service.createPackagingOption(req))
			.isInstanceOf(PackagingOptionAlreadyExistsException.class);
		then(packagingOptionRepository).should(never()).save(any());
	}

	@Test
	@DisplayName("포장 옵션 단건 조회 - 성공")
	void getPackagingOption_success() {
		// given
		PackagingOption option = PackagingOption.builder()
			.name("Box")
			.price(BigDecimal.valueOf(500))
			.build();
		ReflectionTestUtils.setField(option, "id", 2L);
		given(packagingOptionRepository.findById(2L)).willReturn(Optional.of(option));

		// when
		PackagingOptionInfoResponse resp = service.getPackagingOption(2L);

		// then
		assertThat(resp.getId()).isEqualTo(2L);
		assertThat(resp.getName()).isEqualTo("Box");
		assertThat(resp.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(500));
	}

	@Test
	@DisplayName("포장 옵션 단건 조회 - 실패")
	void getPackagingOption_notFound() {
		// given
		given(packagingOptionRepository.findById(anyLong())).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> service.getPackagingOption(99L))
			.isInstanceOf(PackagingOptionNotFoundException.class);
	}

	@Test
	@DisplayName("포장 옵션 검색 조회 - 성공")
	void searchPackagingOption_success() {
		// given
		PackagingOption opt1 = PackagingOption.builder().name("Gift").price(BigDecimal.ONE).build();
		PackagingOption opt2 = PackagingOption.builder().name("Gift Box").price(BigDecimal.TEN).build();
		ReflectionTestUtils.setField(opt1, "id", 3L);
		ReflectionTestUtils.setField(opt2, "id", 4L);
		Pageable pageable = PageRequest.of(0, 10);
		Page<PackagingOption> page = new PageImpl<>(List.of(opt1, opt2), pageable, 2);
		given(packagingOptionRepository.searchNameContaining("Gift", pageable)).willReturn(page);

		// when
		Page<PackagingOptionInfoResponse> result = service.searchPackagingOption("Gift", pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).extracting("id").containsExactly(3L, 4L);
		assertThat(result.getContent()).extracting("name").containsExactly("Gift", "Gift Box");
	}

	@Test
	@DisplayName("포장 옵션 전체 조회 - 성공")
	void getPackagingOptions_success() {
		// given
		PackagingOption opt = PackagingOption.builder().name("Wrap").price(BigDecimal.valueOf(200)).build();
		ReflectionTestUtils.setField(opt, "id", 5L);
		Pageable pageable = PageRequest.of(0, 5);
		Page<PackagingOption> page = new PageImpl<>(List.of(opt), pageable, 1);
		given(packagingOptionRepository.findAll(pageable)).willReturn(page);

		// when
		Page<PackagingOptionInfoResponse> result = service.getPackagingOptions(pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getId()).isEqualTo(5L);
	}

	@Test
	@DisplayName("포장 옵션 수정 - 성공")
	void updatePackagingOption_success() {
		// given
		PackagingOption opt = PackagingOption.builder().name("Old").price(BigDecimal.valueOf(300)).build();
		ReflectionTestUtils.setField(opt, "id", 6L);
		given(packagingOptionRepository.findById(6L)).willReturn(Optional.of(opt));

		PackagingOptionRequest req = PackagingOptionRequest.builder()
			.name("New")
			.price(BigDecimal.valueOf(400))
			.build();

		// when
		PackagingOptionInfoResponse resp = service.updatePackagingOption(6L, req);

		// then
		assertThat(resp.getId()).isEqualTo(6L);
		assertThat(resp.getName()).isEqualTo("New");
		assertThat(resp.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(400));
	}

	@Test
	@DisplayName("포장 옵션 수정 - 실패")
	void updatePackagingOption_notFound() {
		// given
		given(packagingOptionRepository.findById(anyLong())).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> service.updatePackagingOption(100L,
			PackagingOptionRequest.builder().name("X").price(BigDecimal.ZERO).build()))
			.isInstanceOf(PackagingOptionNotFoundException.class);
	}

	@Test
	@DisplayName("포장 옵션 삭제 - 성공")
	void deletePackagingOption_success() {
		// given
		PackagingOption opt = PackagingOption.builder().name("D").price(BigDecimal.ZERO).build();
		ReflectionTestUtils.setField(opt, "id", 7L);
		given(packagingOptionRepository.findById(7L)).willReturn(Optional.of(opt));

		// when
		service.deletePackagingOption(7L);

		// then
		then(packagingOptionRepository).should().delete(opt);
	}

	@Test
	@DisplayName("포장 옵션 삭제 - 실패")
	void deletePackagingOption_notFound() {
		// given
		given(packagingOptionRepository.findById(anyLong())).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> service.deletePackagingOption(999L))
			.isInstanceOf(PackagingOptionNotFoundException.class);
	}
}
