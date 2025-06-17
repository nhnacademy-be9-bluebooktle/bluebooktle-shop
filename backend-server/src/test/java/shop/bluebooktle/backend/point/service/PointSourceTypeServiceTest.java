package shop.bluebooktle.backend.point.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.point.repository.PointSourceTypeRepository;
import shop.bluebooktle.backend.point.service.impl.PointSourceTypeServiceImpl;
import shop.bluebooktle.common.domain.point.ActionType;
import shop.bluebooktle.common.dto.point.request.PointSourceTypeCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointSourceTypeResponse;
import shop.bluebooktle.common.entity.point.PointSourceType;
import shop.bluebooktle.common.exception.point.PointSourceNotFountException;

@ExtendWith(MockitoExtension.class)
class PointSourceTypeServiceTest {

	@InjectMocks
	private PointSourceTypeServiceImpl pointSourceTypeService;

	@Mock
	private PointSourceTypeRepository pointSourceTypeRepository;

	@Test
	@DisplayName("포인트 출처 생성 - 성공")
	void create_success() {
		PointSourceTypeCreateRequest request = new PointSourceTypeCreateRequest(ActionType.EARN, "리뷰 작성");
		PointSourceType savedPst = mock(PointSourceType.class);
		given(savedPst.getId()).willReturn(1L);

		given(pointSourceTypeRepository.save(any(PointSourceType.class))).willReturn(savedPst);

		Long createdId = pointSourceTypeService.create(request);

		ArgumentCaptor<PointSourceType> captor = ArgumentCaptor.forClass(PointSourceType.class);
		verify(pointSourceTypeRepository).save(captor.capture());
		PointSourceType capturedPst = captor.getValue();
		assertThat(capturedPst.getActionType()).isEqualTo(request.actionType());
		assertThat(capturedPst.getSourceType()).isEqualTo(request.sourceType());
		assertThat(createdId).isEqualTo(1L);
	}

	@Test
	@DisplayName("포인트 출처 삭제 - 성공")
	void delete_success() {
		Long idToDelete = 1L;
		given(pointSourceTypeRepository.existsById(idToDelete)).willReturn(true);
		doNothing().when(pointSourceTypeRepository).deleteById(idToDelete);
		pointSourceTypeService.delete(idToDelete);

		verify(pointSourceTypeRepository).deleteById(idToDelete);
	}

	@Test
	@DisplayName("포인트 출처 삭제 실패 - ID를 찾을 수 없음")
	void delete_fail_whenNotFound() {
		Long idToDelete = 99L;
		given(pointSourceTypeRepository.existsById(idToDelete)).willReturn(false);

		assertThrows(PointSourceNotFountException.class, () -> pointSourceTypeService.delete(idToDelete));
		verify(pointSourceTypeRepository, never()).deleteById(anyLong());
	}

	@Test
	@DisplayName("ID로 단건 조회 - 성공")
	void get_success() {
		Long idToFind = 1L;
		PointSourceType pst = mock(PointSourceType.class);
		given(pst.getId()).willReturn(idToFind);
		given(pst.getActionType()).willReturn(ActionType.EARN);
		given(pst.getSourceType()).willReturn("리뷰 작성");

		given(pointSourceTypeRepository.findById(idToFind)).willReturn(Optional.of(pst));

		PointSourceTypeResponse response = pointSourceTypeService.get(idToFind);

		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(idToFind);
		assertThat(response.actionType()).isEqualTo(ActionType.EARN);
		assertThat(response.sourceType()).isEqualTo("리뷰 작성");
	}

	@Test
	@DisplayName("ID로 단건 조회 실패 - ID를 찾을 수 없음")
	void get_fail_whenNotFound() {

		Long idToFind = 99L;
		given(pointSourceTypeRepository.findById(idToFind)).willReturn(Optional.empty());

		assertThrows(PointSourceNotFountException.class, () -> pointSourceTypeService.get(idToFind));
	}

	@Test
	@DisplayName("전체 조회 - 성공")
	void getAll_success() {

		PointSourceType pst1 = mock(PointSourceType.class);
		PointSourceType pst2 = mock(PointSourceType.class);
		given(pointSourceTypeRepository.findAll()).willReturn(List.of(pst1, pst2));

		List<PointSourceTypeResponse> responses = pointSourceTypeService.getAll();

		assertThat(responses)
			.isNotNull()
			.hasSize(2);
	}

	@Test
	@DisplayName("ActionType으로 조회 - 성공")
	void getAllByActionType_success() {

		PointSourceType earnPst1 = mock(PointSourceType.class);
		PointSourceType earnPst2 = mock(PointSourceType.class);

		given(pointSourceTypeRepository.findAllByActionType(ActionType.EARN))
			.willReturn(List.of(earnPst1, earnPst2));

		List<PointSourceTypeResponse> responses = pointSourceTypeService.getAllByActionType(ActionType.EARN);

		assertThat(responses)
			.isNotNull()
			.hasSize(2);
		verify(pointSourceTypeRepository).findAllByActionType(ActionType.EARN);
	}
}