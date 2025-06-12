package shop.bluebooktle.common.exception.elasticsearch;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class ElasticsearchBookDocumentSaveException extends ApplicationException {
	public ElasticsearchBookDocumentSaveException() {
		super(ErrorCode.ELASTIC_SEARCH_SAVE_ERROR);
	}

	public ElasticsearchBookDocumentSaveException(String message) {
		super(ErrorCode.ELASTIC_SEARCH_SAVE_ERROR, message);
	}

	public ElasticsearchBookDocumentSaveException(Throwable cause) {
		super(ErrorCode.ELASTIC_SEARCH_SAVE_ERROR, cause);
	}

	public ElasticsearchBookDocumentSaveException(String message, Throwable cause) {
		super(ErrorCode.ELASTIC_SEARCH_SAVE_ERROR, message, cause);
	}
}
