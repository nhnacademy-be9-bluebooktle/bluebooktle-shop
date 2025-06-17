package shop.bluebooktle.common.exception.elasticsearch;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class ElasticsearchBookNotFoundException extends ApplicationException {

	public ElasticsearchBookNotFoundException() {
		super(ErrorCode.ELASTIC_SEARCH_SAVE_ERROR);
	}

}
