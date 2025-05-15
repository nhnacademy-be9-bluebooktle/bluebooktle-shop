package shop.bluebooktle.backend.book.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.PublisherRequest;
import shop.bluebooktle.backend.book.dto.response.PublisherInfoResponse;
import shop.bluebooktle.backend.book.entity.Publisher;
import shop.bluebooktle.backend.book.repository.BookPublisherRepository;
import shop.bluebooktle.backend.book.repository.PublisherRepository;
import shop.bluebooktle.backend.book.service.PublisherService;
import shop.bluebooktle.common.exception.book.PublisherAlreadyExistsException;
import shop.bluebooktle.common.exception.book.PublisherNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class PublisherServiceImpl implements PublisherService {

	private final PublisherRepository publisherRepository;
	private final BookPublisherRepository bookPublisherRepository;

	@Override
	public void registerPublisher(PublisherRequest request) {
		if (publisherRepository.existsByName(request.getName())) {
			throw new PublisherAlreadyExistsException("출판사명 : " + request.getName());
		}
		Publisher publisher = Publisher.builder()
			.name(request.getName())
			.build();
		publisherRepository.save(publisher);
	}

	@Override
	public void updatePublisher(Long publisherId, PublisherRequest request) {
		Publisher publisher = publisherRepository.findById(publisherId)
			.orElseThrow(() -> new PublisherNotFoundException(publisherId));
		// TODO 출판사를 새로 생성하거나, 이름만 수정할지 의논해야 할 듯
		publisher.setName(request.getName());
		publisherRepository.save(publisher);
	}

	@Override
	@Transactional(readOnly = true)
	public PublisherInfoResponse getPublisher(Long publisherId) {
		Publisher publisher = publisherRepository.findById(publisherId)
			.orElseThrow(() -> new PublisherNotFoundException(publisherId));

		return new PublisherInfoResponse(publisher.getId(), publisher.getName());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PublisherInfoResponse> getPublishers(Pageable pageable) {
		Page<Publisher> publisherPage = publisherRepository.findAll(pageable);
		return publisherPage.map(p -> new PublisherInfoResponse(p.getId(), p.getName()));
	}

	@Override
	public void deletePublisher(Long publisherId) {
		Publisher publisher = publisherRepository.findById(publisherId)
			.orElseThrow(() -> new PublisherNotFoundException(publisherId));
		// 출판사에 등록된 도서 삭제 (도서출판사 연관테이블 삭제)
		// 출판사에 등록된 도서가 있을 경우 삭제 불가
		if (bookPublisherRepository.existsByPublisher(publisher)) {
			throw new RuntimeException("출판사 삭제 불가능");
		}
		publisherRepository.delete(publisher);
	}
}
