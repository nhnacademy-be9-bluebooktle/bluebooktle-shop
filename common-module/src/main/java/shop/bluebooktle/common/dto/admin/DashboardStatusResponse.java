package shop.bluebooktle.common.dto.admin;

public record DashboardStatusResponse(long totalUserCount, long todayOrderCount, long pendingOrderCount) {

}