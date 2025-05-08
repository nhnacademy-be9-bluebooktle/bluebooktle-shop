package shop.bluebooktle.auth.service;

public interface RefreshTokenService {

	public void save(Long userId, String refreshToken, long ttlMillis);

	public String findByUserId(Long userId);

	public void deleteByUserId(Long userId);
	
}