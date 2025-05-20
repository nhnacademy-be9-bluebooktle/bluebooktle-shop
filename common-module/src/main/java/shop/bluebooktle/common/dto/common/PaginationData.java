package shop.bluebooktle.common.dto.common;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationData<T> {
	private List<T> content;
	private PaginationInfo pagination;

	public PaginationData(Page<T> page) {
		this.content = page.getContent();
		this.pagination = new PaginationInfo(page);
	}

	public long getTotalElements() {
		return pagination.totalElements;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	private static class PaginationInfo {
		private int totalPages;
		private long totalElements;
		private int currentPage;
		private int pageSize;
		private boolean isFirst;
		private boolean isLast;
		private boolean hasNext;
		private boolean hasPrevious;

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
