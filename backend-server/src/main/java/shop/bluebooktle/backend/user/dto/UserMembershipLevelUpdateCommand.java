package shop.bluebooktle.backend.user.dto;

public record UserMembershipLevelUpdateCommand(
	Long userId,
	Long membershipId) {
}
