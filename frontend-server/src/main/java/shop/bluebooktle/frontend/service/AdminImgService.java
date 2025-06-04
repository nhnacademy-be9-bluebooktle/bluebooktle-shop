package shop.bluebooktle.frontend.service;

public interface AdminImgService {
	String getPresignedUploadUrl();

	void deleteMinioUrl(String fileName);

}
