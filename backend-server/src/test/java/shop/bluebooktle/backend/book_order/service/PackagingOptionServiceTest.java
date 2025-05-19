package shop.bluebooktle.backend.book_order.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.book_order.repository.PackagingOptionRepository;
import shop.bluebooktle.backend.book_order.service.impl.PackagingOptionServiceImpl;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionRequest;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionUpdateRequest;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionResponse;
import shop.bluebooktle.common.exception.book_order.PackagingOptionNotFoundException;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class PackagingOptionServiceTest {
	@InjectMocks
	private PackagingOptionServiceImpl packagingOptionService;

	@Mock
	private PackagingOptionRepository packagingOptionRepository;

	@Test
	@DisplayName("포장 옵션 등록 - 성공")
	void createPackagingOption_success() {
		// given
		PackagingOptionRequest request = PackagingOptionRequest.builder()
			.name("기본 포장지")
			.price(BigDecimal.valueOf(1500))
			.build();

		PackagingOption responseSaved = PackagingOption.builder() // DTO 답변 비교하기 위함
			.name("기본 포장지")
			.price(BigDecimal.valueOf(1500))
			.build();
		responseSaved.setId(1L);

		when(packagingOptionRepository.existsByName("기본 포장지")).thenReturn(false);
		when(packagingOptionRepository.save(any())).thenReturn(responseSaved);

		// when
		PackagingOptionResponse response = packagingOptionService.createPackagingOption(request);

		// then
		assertThat(response.getPackagingOptionId()).isEqualTo(1L);
		assertThat(response.getName()).isEqualTo("기본 포장지");
		assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(1500));
	}

	@Test
	@DisplayName("포장 옵션 등록 실패 - 이름 중복")
	void createPackagingOption_duplicateName() {
		// given
		PackagingOptionRequest request = PackagingOptionRequest.builder()
			.name("중복 포장지")
			.price(BigDecimal.valueOf(2000))
			.build();

		when(packagingOptionRepository.existsByName("중복 포장지")).thenReturn(true);

		// then
		assertThatThrownBy(() -> packagingOptionService.createPackagingOption(request))
			.isInstanceOf(PackagingOptionNotFoundException.class);
	}

	@Test
	@DisplayName("포장 옵션 수정 성공")
	void updatePackagingOption_success() {
		// given
		PackagingOption option = PackagingOption.builder()
			.name("기존 포장지")
			.price(BigDecimal.valueOf(1000))
			.build();
		option.setId(1L);

		PackagingOptionUpdateRequest request = PackagingOptionUpdateRequest.builder()
			.name("수정된 포장지")
			.price(BigDecimal.valueOf(3000))
			.build();

		when(packagingOptionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(option));

		// when
		PackagingOptionResponse response = packagingOptionService.updatePackagingOption(1L, request);

		// then
		assertThat(response.getName()).isEqualTo("수정된 포장지");
		assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(3000));
	}

	@Test
	@DisplayName("포장 옵션 삭제 - 성공")
	void deletePackagingOption_success() {
		// given
		PackagingOption option = PackagingOption.builder()
			.name("삭제 포장지")
			.price(BigDecimal.valueOf(500))
			.build();
		option.setId(1L);

		when(packagingOptionRepository.findById(1L)).thenReturn(Optional.of(option));

		// when
		packagingOptionService.deletePackagingOption(1L);

		// then
		verify(packagingOptionRepository).delete(option);
	}

	@Test
	@DisplayName("포장 옵션 삭제 실패 - 존재하지 않음")
	void deletePackagingOption_notFound() {
		when(packagingOptionRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> packagingOptionService.deletePackagingOption(1L))
			.isInstanceOf(PackagingOptionNotFoundException.class);
	}
}
