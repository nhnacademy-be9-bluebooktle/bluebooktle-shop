package shop.bluebooktle.auth.service;

import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;

public interface PointService {

	void adjustUserPointAndSavePointHistory(Long userId, PointSourceTypeEnum pointSourceTypeEnum);
}
