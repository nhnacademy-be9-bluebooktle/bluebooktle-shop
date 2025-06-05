package shop.bluebooktle.frontend.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.frontend.repository.AdminImgRepository;
import shop.bluebooktle.frontend.service.AdminImgService;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminImgServiceImpl implements AdminImgService {

	private final AdminImgRepository adminImgRepository;

	public String getPresignedUploadUrl() {
		UUID fileName = UUID.randomUUID();
		log.info("file name: {}", fileName);
		return adminImgRepository.getPresignedUploadUrl(fileName.toString());
	}

	@Override
	public void deleteImage(String fileName) {
		adminImgRepository.deleteImage(fileName);
	}
}
