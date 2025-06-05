package shop.bluebooktle.frontend.service;

public interface AdminImgService {
	String getPresignedUploadUrl();

	void deleteImage(String fileName);

}
