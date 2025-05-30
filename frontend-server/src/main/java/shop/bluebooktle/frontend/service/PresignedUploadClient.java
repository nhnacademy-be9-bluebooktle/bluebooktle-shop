package shop.bluebooktle.frontend.service;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface PresignedUploadClient {
	@RequestLine("PUT")
	@Headers("Content-Type: {contentType}")
	void upload(@Param("contentType") String contentType, byte[] payload);
}