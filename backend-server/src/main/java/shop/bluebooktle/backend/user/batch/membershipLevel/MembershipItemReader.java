package shop.bluebooktle.backend.user.batch.membershipLevel;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.dto.UserNetSpentAmountDto;
import shop.bluebooktle.backend.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class MembershipItemReader implements ItemReader<UserNetSpentAmountDto> {

	private final UserRepository userRepository;
	private Iterator<UserNetSpentAmountDto> iterator;

	@Override
	public UserNetSpentAmountDto read() {
		if (iterator == null) {
			List<UserNetSpentAmountDto> data = userRepository.findUserNetSpentAmountForLastThreeMonths();
			log.info("등급 갱신 대상 회원 수: {}", data.size());
			iterator = data.iterator();
		}
		log.info("등급 변경할 유저 조회 결과 없음");
		return iterator.hasNext() ? iterator.next() : null;
	}
}
