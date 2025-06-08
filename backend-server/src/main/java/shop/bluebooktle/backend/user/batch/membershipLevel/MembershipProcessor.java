package shop.bluebooktle.backend.user.batch.membershipLevel;

import java.math.BigDecimal;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.dto.UserMembershipLevelUpdateCommand;
import shop.bluebooktle.backend.user.dto.UserNetSpentAmountDto;
import shop.bluebooktle.backend.user.repository.MembershipLevelRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.entity.auth.MembershipLevel;

@Component
@RequiredArgsConstructor
@Slf4j
public class MembershipProcessor implements ItemProcessor<UserNetSpentAmountDto, UserMembershipLevelUpdateCommand> {

	private final MembershipLevelRepository membershipLevelRepository;
	private final UserRepository userRepository;

	@Override
	public UserMembershipLevelUpdateCommand process(UserNetSpentAmountDto item) {
		Long userId = item.userId();
		BigDecimal netAmount = item.netAmount();

		MembershipLevel newMembershipLevel = membershipLevelRepository.findByMembershipLevelForAmount(netAmount);
		Long currentMembershipId = userRepository.findMembershipIdById(userId);

		if (!newMembershipLevel.getId().equals(currentMembershipId)) {
			log.info("등급 변경 대상: userId={}, from={} to={}", userId, currentMembershipId, newMembershipLevel.getId());
			return new UserMembershipLevelUpdateCommand(userId, newMembershipLevel.getId());
		} else {
			log.info("등급 유지: userId={}, 등급={}", userId, currentMembershipId);
		}
		return null;
	}
}
