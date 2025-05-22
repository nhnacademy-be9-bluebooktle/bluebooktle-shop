package shop.bluebooktle.auth.service;

public interface AccessTokenService {
	public boolean isBlacklisted(String accessToken);

	public void addToBlacklist(String accessToken);
}