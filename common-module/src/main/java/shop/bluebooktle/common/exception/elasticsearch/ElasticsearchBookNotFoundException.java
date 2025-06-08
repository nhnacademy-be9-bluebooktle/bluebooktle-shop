package shop.bluebooktle.common.exception.elasticsearch;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class ElasticsearchBookNotFoundException extends ApplicationException {
	
	public ElasticsearchBookNotFoundException() {
		super(ErrorCode.ELASTIC_SEARCH_SAVE_ERROR);
	}

	public ElasticsearchBookNotFoundException(String message) {
		super(ErrorCode.ELASTIC_SEARCH_SAVE_ERROR, message);
	}

	public ElasticsearchBookNotFoundException(Throwable cause) {
		super(ErrorCode.ELASTIC_SEARCH_SAVE_ERROR, cause);
	}

	public ElasticsearchBookNotFoundException(String message, Throwable cause) {
		super(ErrorCode.ELASTIC_SEARCH_SAVE_ERROR, message, cause);
	}

}
