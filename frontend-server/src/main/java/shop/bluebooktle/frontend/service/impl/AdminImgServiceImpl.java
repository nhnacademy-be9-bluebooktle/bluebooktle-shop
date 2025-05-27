package shop.bluebooktle.frontend.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.frontend.repository.AdminImgRepository;
import shop.bluebooktle.frontend.service.AdminImgService;

@RequiredArgsConstructor
@Service
public class AdminImgServiceImpl implements AdminImgService {

	private final AdminImgRepository adminImgRepository;

	public String getPresignedUploadUrl() {
		UUID fileName = UUID.randomUUID();

		return adminImgRepository.getPresignedUploadUrl(fileName.toString());
	}
}
