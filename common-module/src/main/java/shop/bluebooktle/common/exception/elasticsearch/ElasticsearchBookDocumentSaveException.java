package shop.bluebooktle.common.exception.elasticsearch;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class ElasticsearchBookDocumentSaveException extends ApplicationException {

	public ElasticsearchBookDocumentSaveException(String message) {
		super(ErrorCode.ELASTIC_SEARCH_SAVE_ERROR, message);
	}

}
