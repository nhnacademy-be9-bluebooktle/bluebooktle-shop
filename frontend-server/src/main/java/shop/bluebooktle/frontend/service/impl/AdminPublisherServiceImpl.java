package shop.bluebooktle.frontend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.request.PublisherRequest;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
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

		try {
			PaginationData<PublisherInfoResponse> data = publisherRepository.getPublishers(page, size, keyword);
			return new PageImpl<>(data.getContent(), pageable, data.getTotalElements());
		} catch (Exception e) {
			throw new PublisherListFetchException();
		}
	}

	@Override
	public PublisherInfoResponse getPublisher(Long id) {
		try {
			return publisherRepository.getPublisher(id);
		} catch (Exception e) {
			throw new PublisherNotFoundException();
		}
	}

	@Override
	public void createPublisher(PublisherRequest request) {
		try {
			publisherRepository.createPublisher(request);
		} catch (Exception e) {
			throw new PublisherCreateException();
		}
	}

	@Override
	public void updatePublisher(Long id, PublisherRequest request) {
		try {
			publisherRepository.updatePublisher(id, request);
		} catch (Exception e) {
			throw new PublisherUpdateException();
		}
	}

	@Override
	public void deletePublisher(Long id) {
		try {
			publisherRepository.deletePublisher(id);
		} catch (Exception e) {
			throw new PublisherDeleteException();
		}
	}
}
