package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.request.PublisherRequest;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.exception.book.PublisherCreateException;
import shop.bluebooktle.common.exception.book.PublisherDeleteException;
import shop.bluebooktle.common.exception.book.PublisherListFetchException;
import shop.bluebooktle.common.exception.book.PublisherNotFoundException;
import shop.bluebooktle.common.exception.book.PublisherUpdateException;
import shop.bluebooktle.frontend.repository.AdminPublisherRepository;
import shop.bluebooktle.frontend.service.AdminPublisherService;

@Service
@RequiredArgsConstructor
public class AdminPublisherServiceImpl implements AdminPublisherService {
	private final AdminPublisherRepository publisherRepository;

	@Override
	public Page<PublisherInfoResponse> getPublishers(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size);

		String keyword = null;
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			keyword = searchKeyword;
		}

		JsendResponse<PaginationData<PublisherInfoResponse>> response = publisherRepository.getPublishers(page, size,
			keyword);
		if (!"success".equalsIgnoreCase(response.status())) {
			throw new PublisherListFetchException();
		}

		PaginationData<PublisherInfoResponse> data = response.data();
		List<PublisherInfoResponse> tags = data.getContent();
		return new PageImpl<>(tags, pageable, data.getTotalElements());
	}

	@Override
	public PublisherInfoResponse getPublisher(Long id) {
		JsendResponse<PublisherInfoResponse> response = publisherRepository.getPublisher(id);
		if (!"success".equalsIgnoreCase(response.status())) {
			throw new PublisherNotFoundException();
		}
		return response.data();
	}

	@Override
	public void createPublisher(PublisherRequest request) {
		JsendResponse<Void> response = publisherRepository.createPublisher(request);
		if (!"success".equalsIgnoreCase(response.status())) {
			throw new PublisherCreateException();
		}
	}

	@Override
	public void updatePublisher(Long id, PublisherRequest request) {
		JsendResponse<Void> response = publisherRepository.updatePublisher(id, request);
		if (!"success".equalsIgnoreCase(response.status())) {
			throw new PublisherUpdateException();
		}
	}

	@Override
	public void deletePublisher(Long id) {
		JsendResponse<Void> response = publisherRepository.deletePublisher(id);
		if (!"success".equalsIgnoreCase(response.status())) {
			throw new PublisherDeleteException();
		}
	}
}
