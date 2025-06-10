package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.membership.MembershipLevelDetailDto;
import shop.bluebooktle.frontend.repository.MembershipLevelRepository;
import shop.bluebooktle.frontend.service.MembershipLevelService;

@Service
@RequiredArgsConstructor
public class MembershipLevelServiceImpl implements MembershipLevelService {

	private final MembershipLevelRepository membershipLevelRepository;

	@Override
	public List<MembershipLevelDetailDto> getMembershipLevels() {
		return membershipLevelRepository.getMembershipLevels();
	}
}
