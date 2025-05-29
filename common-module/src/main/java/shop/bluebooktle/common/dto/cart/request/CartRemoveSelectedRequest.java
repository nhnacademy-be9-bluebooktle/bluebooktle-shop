package shop.bluebooktle.common.dto.cart.request;

import java.util.List;

public record CartRemoveSelectedRequest(String id, List<Long> bookIds) {
}
