package shop.bluebooktle.backend.user.batch.membershipLevel;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.dto.UserMembershipLevelUpdateCommand;
import shop.bluebooktle.backend.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class MembershipWriter implements ItemWriter<UserMembershipLevelUpdateCommand> {

	private final UserRepository userRepository;

	@Override
	public void write(Chunk<? extends UserMembershipLevelUpdateCommand> chunk) {
		for (UserMembershipLevelUpdateCommand command : chunk) {
			userRepository.updateMembershipLevel(command.userId(), command.membershipId());
			log.info("userId = {}, membershipId = {}", command.userId(), command.membershipId());
		}
		log.info("등급 변경 회원 수 : {}", chunk.size());
	}
}
