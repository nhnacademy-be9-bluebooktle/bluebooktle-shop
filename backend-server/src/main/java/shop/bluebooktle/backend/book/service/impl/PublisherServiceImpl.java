package shop.bluebooktle.backend.book.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.PublisherRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.PublisherUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.PublisherInfoResponse;
import shop.bluebooktle.backend.book.entity.Publisher;
import shop.bluebooktle.backend.book.repository.PublisherRepository;
import shop.bluebooktle.backend.book.service.PublisherService;
import shop.bluebooktle.common.exception.book.PublisherAlreadyExistsException;
import shop.bluebooktle.common.exception.book.PublisherNotFoundException;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

	private final PublisherRepository publisherRepository;

	@Override
	@Transactional
	public void addPublisher(PublisherRegisterRequest request) {
		if (publisherRepository.existsByName(request.getName())) {
			throw new PublisherAlreadyExistsException("출판사명" + request.getName());
		}
		Publisher publisher = Publisher.builder()
			.name(request.getName())
			.build();
		publisherRepository.save(publisher);
	}

	@Override
	@Transactional
	public void updatePublisher(PublisherUpdateRequest request) {
		if (!publisherRepository.existsById(request.getPublisherId())) {
			throw new PublisherNotFoundException(request.getPublisherId());
		}

		Publisher publisher = publisherRepository.findById(request.getPublisherId()).get();
		publisher.setName(request.getName());
		publisherRepository.save(publisher);
	}

	@Override
	@Transactional(readOnly = true)
	public PublisherInfoResponse getPublisher(Long publisherId) {
		if (!publisherRepository.existsById(publisherId)) {
			throw new PublisherNotFoundException(publisherId);
		}

		Publisher publisher = publisherRepository.findById(publisherId).get();
		return new PublisherInfoResponse(publisher.getId(), publisher.getName());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PublisherInfoResponse> getPublishers(Pageable pageable) {
		Page<Publisher> publisherPage = publisherRepository.findAll(pageable);

		return publisherPage.map(p -> new PublisherInfoResponse(p.getId(), p.getName()));
	}

	@Override
	@Transactional
	public void deletePublisher(Long publisherId) {
		if (!publisherRepository.existsById(publisherId)) {
			throw new PublisherNotFoundException(publisherId);
		}

		Publisher publisher = publisherRepository.findById(publisherId).get();
		publisherRepository.delete(publisher);
	}
}
