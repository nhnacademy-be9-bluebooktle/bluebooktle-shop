package shop.bluebooktle.frontend.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.admin.DashboardStatusResponse;
import shop.bluebooktle.frontend.repository.DashboardRepository;
import shop.bluebooktle.frontend.service.DashboardService;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

	private final DashboardRepository dashboardRepository;

	@Override
	public DashboardStatusResponse getDashboardStatus() {
		return dashboardRepository.getDashboardStatus();
	}
}