package shop.bluebooktle.common.dto.common;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class PaginationData<T> {
	private List<T> content;
	private PaginationInfo pagination;

	public PaginationData(Page<T> page) {
		this.content = page.getContent();
		this.pagination = new PaginationInfo(page);
	}

	@Getter
	private static class PaginationInfo {
		private final int totalPages;
		private final long totalElements;
		private final int currentPage;
		private final int pageSize;
		private final boolean isFirst;
		private final boolean isLast;
		private final boolean hasNext;
		private final boolean hasPrevious;

		public PaginationInfo(Page<?> page) {
			this.totalPages = page.getTotalPages();
			this.totalElements = page.getTotalElements();
			this.currentPage = page.getNumber();
			this.pageSize = page.getSize();
			this.isFirst = page.isFirst();
			this.isLast = page.isLast();
			this.hasNext = page.hasNext();
			this.hasPrevious = page.hasPrevious();
		}
	}
}