package shop.bluebooktle.backend.book.service;

public interface MinioService {

	String getPresignedUploadUrl(String fileName);

	void deleteImage(String fileName);
}
