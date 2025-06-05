package shop.bluebooktle.backend.user.service;

public interface DormantAuthCodeService {
	String generateAndSaveAuthCode(Long userId);

	boolean verifyAuthCode(Long userId, String submittedCode);

	void deleteAuthCode(Long userId);
}