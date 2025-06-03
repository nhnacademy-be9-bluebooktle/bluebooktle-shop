package shop.bluebooktle.common.dto.cart.request;

import java.util.List;

public record BookIdListRequest(List<Long> bookIds) {
}
