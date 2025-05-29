package shop.bluebooktle.auth.service;

import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;

public interface PointService {

	void adjustUserPointAndSavePointHistory(Long userId, PointSourceTypeEnum pointSourceTypeEnum);

	void signUpPoint(Long userId);

	void loginPoint(Long userId);
}
