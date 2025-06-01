package shop.bluebooktle.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// Common Errors (공통 오류) - C
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다."),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "유효하지 않은 입력 값입니다."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 리소스를 찾을 수 없습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C004", "허용되지 않은 HTTP 메소드입니다."),
	HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C005", "접근 권한이 없습니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C006", "인증 정보가 없거나 유효하지 않습니다."),
	TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "C007", "요청 값의 타입이 올바르지 않습니다."),
	MEDIA_TYPE_NOT_SUPPORTED(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "C008", "지원되지 않는 미디어 타입입니다."),
	BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "C009", "외부 서비스 연동 중 오류가 발생했습니다."),

	// Auth & User Errors (인증/회원 오류) - A
	AUTH_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A001", "아이디 또는 비밀번호가 일치하지 않습니다."),
	AUTH_LOGIN_ID_ALREADY_EXISTS(HttpStatus.CONFLICT, "A002", "이미 사용 중인 아이디입니다."),
	AUTH_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "A003", "이미 사용 중인 이메일입니다."),
	AUTH_INACTIVE_ACCOUNT(HttpStatus.UNAUTHORIZED, "A004", "비활성화 상태 계정입니다. 인증 후 활성화 해주세요."),
	AUTH_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "유효하지 않은 인증정보입니다."),
	AUTH_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "A006", "사용자를 찾을 수 없습니다."),
	AUTH_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "A007", "현재 비밀번호가 일치하지 않습니다."),
	AUTH_PASSWORD_ENCRYPTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "A008", "비밀번호 암호화 중 오류가 발생했습니다."),
	AUTH_TOKEN_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "A009", "토큰 발행 중 오류가 발생했습니다."),
	AUTH_TOKEN_VALIDATION_FAILED(HttpStatus.UNAUTHORIZED, "A010", "유효하지 않거나 만료된 토큰입니다."),
	AUTH_ACCOUNT_WITHDRAWN(HttpStatus.FORBIDDEN, "A011", "탈퇴한 계정입니다."),
	AUTH_NOT_DORMANT_ACCOUNT(HttpStatus.BAD_REQUEST, "A012", "휴면 상태가 아닌 계정입니다."),
	AUTH_ADDRESS_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "A013", "주소는 최대 10개까지 등록할 수 있습니다."),
	AUTH_INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "A014", "유효하지 않은 주소 정보입니다."),
	AUTH_MEMBERSHIP_LEVEL_NOT_FOUND(HttpStatus.NOT_FOUND, "A015", "회원 등급 정보를 찾을 수 없습니다."),
	AUTH_INVALID_USER_ID(HttpStatus.BAD_REQUEST, "A016", "유효하지 않은 계정 ID입니다."),
	AUTH_TOKEN_REISSUE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "A017", "토큰 재발급에 실패했습니다. "),
	USER_MEMBERSHIP_NOT_FOUNT(HttpStatus.BAD_REQUEST, "A018", "회원 등급을 찾을 수 없습니다. "),
	AUTH_OAUTH_LOGIN_FAILED(HttpStatus.BAD_REQUEST, "A019", "OAUTH 로그인에 실패했습니다."),

	// Book Errors (도서 관련 오류) - B
	BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "도서를 찾을 수 없습니다."),
	BOOK_INVALID_ISBN(HttpStatus.BAD_REQUEST, "B002", "유효하지 않은 ISBN 입니다."),
	PUBLISHER_NOT_FOUND(HttpStatus.NOT_FOUND, "B003", "출판사를 찾을 수 없습니다."),
	BOOK_AUTHOR_NOT_FOUND(HttpStatus.NOT_FOUND, "B004", "작가를 찾을 수 없습니다."),
	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "B005", "카테고리를 찾을 수 없습니다."),
	TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "B006", "태그를 찾을 수 없습니다."),
	BOOK_ALREADY_LIKED(HttpStatus.CONFLICT, "B007", "이미 좋아요를 누른 도서입니다."),
	BOOK_NOT_LIKED(HttpStatus.BAD_REQUEST, "B008", "좋아요 상태가 아닌 도서입니다."), // 좋아요 취소 시
	BOOK_SALE_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "B009", "도서 판매 정보를 찾을 수 없습니다."),
	BOOK_STATE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "B010", "현재 구매할 수 없는 도서입니다."), // 재고 부족, 판매 종료 등
	BOOK_ALREADY_EXISTS_EXCEPTION(HttpStatus.CONFLICT, "B011", "이미 등록된 도서입니다."),
	BOOK_SALE_INFO_ALREADY_EXISTS(HttpStatus.CONFLICT, "B012", "이미 등록된 도서 판매 정보입니다."),
	BOOK_ID_NULL(HttpStatus.BAD_REQUEST, "B013", "도서 ID는 필수입니다."),
	PUBLISHER_LIST_FETCH_ERROR(HttpStatus.NOT_FOUND, "B014", "출판사를 찾을 수 없습니다."),
	PUBLISHER_CREATE_FAILED(HttpStatus.BAD_GATEWAY, "B014", "출판사 등록에 실패했습니다."),
	PUBLISHER_UPDATE_FAILED(HttpStatus.BAD_GATEWAY, "B015", "출판사 수정에 실패했습니다."),
	PUBLISHER_DELETE_FAILED(HttpStatus.BAD_GATEWAY, "B016", "출판사 삭제에 실패했습니다."),

	// Book - 카테고리
	BOOK_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "BC01", "도서에 등록된 카테고리를 찾을 수 없습니다."),
	CATEGORY_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "BC002", "이미 존재하는 카테고리명입니다. "), // 이미 존재하는 카테고리명일 시
	CATEGORY_DELETE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "BC03", "해당 카테고리는 삭제할 수 없습니다."),
	CATEGORY_REQUIRED(HttpStatus.BAD_REQUEST, "BC007", "2단계 카테고리는 최소 1개 존재해야 합니다."),
	BOOK_CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "BC04", "카테고리에 이미 등록된 도서입니다."),
	BOOK_CATEGORY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "BC05", "카테고리는 최대 10개까지 등록할 수 있습니다."),
	BOOK_CATEGORY_REQUIRED(HttpStatus.BAD_REQUEST, "BC06", "도서는 카테고리를 최소 1개 이상 가져야 합니다."),

	// Book - 출판사
	PUBLISHER_ALREADY_EXISTS(HttpStatus.CONFLICT, "BP01", "이미 등록된 출판사입니다."),
	BOOK_PUBLISHER_NOT_FOUND(HttpStatus.NOT_FOUND, "BP02", "도서에 등록된 출판사를 찾을 수 없습니다."),
	BOOK_PUBLISHER_ALREADY_EXISTS(HttpStatus.CONFLICT, "BP03", "도서에 이미 등록된 출판사입니다."),
	PUBLISHER_DELETE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "BP04", "도서에 등록된 출판사로 삭제할 수 없습니다."),
	// Book - 태그
	TAG_ALREADY_EXISTS(HttpStatus.CONFLICT, "BT01", "이미 등록된 태그입니다."),
	BOOK_TAG_ALREADY_EXISTS(HttpStatus.CONFLICT, "BT03", "도서에 이미 등록된 태그입니다."),
	BOOK_TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "BP02", "도서에 등록된 태그를 찾을 수 없습니다."),
	TAG_LIST_FETCH_ERROR(HttpStatus.BAD_GATEWAY, "BT04", "태그 목록 조회 중 오류가 발생했습니다."),
	TAG_CREATE_FAILED(HttpStatus.BAD_GATEWAY, "BT05", "태그 등록에 실패했습니다."),
	TAG_UPDATE_FAILED(HttpStatus.BAD_GATEWAY, "BT06", "태그 수정에 실패했습니다."),
	TAG_DELETE_FAILED(HttpStatus.BAD_GATEWAY, "BT07", "태그 삭제에 실패했습니다."),

	// Book Order (도서 주문 포장 오류) - G (Gift)
	G_BOOK_ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "도서 주문 정보를 찾을 수 없습니다."),
	G_BOOK_ORDER_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "G002", "도서 주문 정보 수정에 실패했습니다."),
	G_BOOK_ORDER_INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "G003", "도서 주문 수량이 유효하지 않습니다."),
	G_BOOK_ORDER_INVALID_PRICE(HttpStatus.BAD_REQUEST, "G004", "도서 주문 가격이 유효하지 않습니다."),
	G_ORDER_PACKAGING_NOT_FOUND(HttpStatus.NOT_FOUND, "G005", "도서 주문 포장 정보를 찾을 수 없습니다."),
	G_ORDER_PACKAGING_ALREADY_EXISTS(HttpStatus.CONFLICT, "G006", "이미 해당 도서 주문에 포장 옵션이 등록되어 있습니다."),
	G_ORDER_PACKAGING_INVALID_OPTION(HttpStatus.BAD_REQUEST, "G007", "유효하지 않은 포장 옵션입니다."),
	G_ORDER_PACKAGING_INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "G008", "포장 수량은 1개 이상이어야 합니다."),
	G_PACKAGING_QUANTITY_EXCEEDS_BOOK_ORDER(HttpStatus.BAD_REQUEST, "G009", "포장 수량은 도서 주문 수량을 초과할 수 없습니다."),
	G_ORDER_PACKAGING_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "G010", "포장 정보 삭제 중 오류가 발생했습니다."),
	G_ORDER_PACKAGING_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "G011", "주문 포장 옵션을 찾을 수 없습니다."),
	G_ORDER_PACKAGING_OPTION_ALREADY_EXITS(HttpStatus.CONFLICT, "G012", "이미 해당 포장 옵션이 등록되어 있습니다."),

	// 도서 - 이미지
	BOOK_IMG_BOOK_ID_NULL(HttpStatus.BAD_REQUEST, "BI01", "도서 ID는 필수입니다."),
	BOOK_IMG_IMAGE_ID_NULL(HttpStatus.BAD_REQUEST, "BI02", "이미지 ID는 필수입니다."),
	BOOK_IMG_ALREADY_EXISTS(HttpStatus.CONFLICT, "BI03", "이미 등록된 도서-이미지 매핑입니다."),
	BOOK_IMG_NOT_FOUND(HttpStatus.NOT_FOUND, "BI04", "도서-이미지 매핑 정보를 찾을 수 없습니다."),

	// Author - 작가
	AUTHOR_FIELD_NULL(HttpStatus.BAD_REQUEST, "BA01", "작가 등록에 필요한 필드(name, description, authorKey)는 null 일 수 없습니다."),
	AUTHOR_ID_NULL(HttpStatus.BAD_REQUEST, "BA02", "작가 ID는 필수입니다."),
	AUTHOR_ALREADY_EXISTS(HttpStatus.CONFLICT, "BA03", "이미 존재하는 작가 이름입니다."),
	AUTHOR_NOT_FOUND(HttpStatus.NOT_FOUND, "BA04", "존재하지 않는 작가입니다."),
	AUTHOR_UPDATE_FIELD_MISSING(HttpStatus.BAD_REQUEST, "BA05",
		"적어도 하나 이상의 필드(name, description, authorKey)를 제공해야 합니다."),

	// 도서 - 작가
	BOOK_AUTHOR_ALREADY_EXISTS(HttpStatus.CONFLICT, "BA06", "이미 등록된 도서-작가 매핑입니다."),

	IMAGE_ID_NULL(HttpStatus.BAD_REQUEST, "B015", "이미지 ID는 필수입니다."),
	IMAGE_URL_EMPTY(HttpStatus.BAD_REQUEST, "B016", "이미지 URL은 비어 있을 수 없습니다."),
	IMAGE_ALREADY_EXISTS(HttpStatus.CONFLICT, "B017", "이미지가 이미 존재합니다."),
	IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "B018", "이미지를 찾을 수 없습니다."),

	// Cart Errors (장바구니 오류) - T (TeaCart)
	CART_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "장바구니를 찾을 수 없습니다."), // 예외적인 경우
	CART_BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "T002", "장바구니에서 해당 도서를 찾을 수 없습니다."),
	CART_BOOK_ADD_FAILED(HttpStatus.BAD_REQUEST, "T003", "장바구니에 담을 수 없는 도서입니다."), // 판매 상태 등
	CART_INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "T004", "장바구니 수량이 유효하지 않습니다."),
	CART_IS_EMPTY(HttpStatus.BAD_REQUEST, "T005", "장바구니가 비어있습니다."),
	GUEST_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "T006", "GUEST-ID가 유효하지 않습니다."),

	// Order Errors (주문 오류) - O
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
	ORDER_INVALID_STATE(HttpStatus.BAD_REQUEST, "O002", "현재 주문 상태에서는 요청을 처리할 수 없습니다."), // 취소/반품 불가 상태
	ORDER_BOOK_NOT_ORDERABLE(HttpStatus.BAD_REQUEST, "O003", "주문하려는 도서 중 구매 불가능한 상품이 있습니다."), // 품절, 판매 종료 등
	ORDER_DELIVERY_DATE_INVALID(HttpStatus.BAD_REQUEST, "O004", "유효하지 않은 배송 날짜입니다."),
	ORDER_PACKAGING_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "O005", "포장 옵션을 찾을 수 없습니다."),
	ORDER_DELIVERY_RULE_NOT_FOUND(HttpStatus.NOT_FOUND, "O006", "배송 정책을 찾을 수 없습니다."),
	ORDER_INVALID_ORDER_KEY(HttpStatus.UNAUTHORIZED, "O007", "유효하지 않은 주문 확인 정보입니다."), // 비회원 주문 확인
	ORDER_STATE_NOT_FOUND(HttpStatus.NOT_FOUND, "O008", "해당 주문상태가 존재하지 않습니다."),

	// Payment & Point Errors (결제/포인트 오류) - P
	PAYMENT_FAILED(HttpStatus.BAD_GATEWAY, "P001", "결제 시스템 오류가 발생했습니다."), // 외부 결제사 오류
	PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P002", "결제 정보를 찾을 수 없습니다."),
	PAYMENT_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "P003", "결제 수단을 찾을 수 없습니다."),
	PAYMENT_TYPE_ALREADY_EXISTS(HttpStatus.CONFLICT, "P004", "이미 존재하는 결제수단입니다."),
	POINT_INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "P005", "포인트 잔액이 부족합니다."),
	POINT_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "P006", "포인트 이력을 찾을 수 없습니다."),
	POINT_SOURCE_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "P007", "포인트 발생/사용 타입을 찾을 수 없습니다."),
	POINT_POLICY_CREATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "P008", "포인트 정책 생성이 허용되지 않습니다."),
	POINT_POLICY_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "P009", "비활성화된 포인트 정책입니다."),
	PAYMENT_INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "P010", "결제 금액이 유효하지 않습니다."),
	POINT_POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "P011", "포인트 정책을 찾을 수 없습니다."),
	POINT_POLICY_ALREADY_EXISTS(HttpStatus.CONFLICT, "P012", "이미 존재하는 포인트 정책입니다."),
	REFUND_NOT_POSSIBLE(HttpStatus.BAD_REQUEST, "P013", "현재 상태에서는 반품/환불이 불가능합니다."),
	REFUND_ALREADY_PROCESSED(HttpStatus.CONFLICT, "P0014", "이미 처리된 반품/환불 요청입니다."),
	REFUND_INVALID_REASON(HttpStatus.BAD_REQUEST, "P015", "유효하지 않은 반품 사유입니다."),
	REFUND_NOT_FOUND(HttpStatus.NOT_FOUND, "P016", "환불 정보를 찾을 수 없습니다."),

	// Coupon Errors (쿠폰 오류) - K (Koopon)
	K_COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "K001", "쿠폰을 찾을 수 없습니다."),
	K_USER_COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "K002", "사용자의 쿠폰을 찾을 수 없습니다."),
	K_COUPON_ALREADY_USED(HttpStatus.CONFLICT, "K003", "이미 사용된 쿠폰입니다."),
	K_COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "K004", "사용 기간이 만료된 쿠폰입니다."),
	K_COUPON_NOT_APPLICABLE(HttpStatus.BAD_REQUEST, "K005", "해당 주문/상품에 적용할 수 없는 쿠폰입니다."), // 최소 금액, 대상 상품/카테고리 불일치 등
	K_COUPON_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "K006", "쿠폰 타입을 찾을 수 없습니다."),
	K_INVALID_COUPON_POLICY(HttpStatus.INTERNAL_SERVER_ERROR, "K007", "유효하지 않은 쿠폰 정책 설정입니다."),
	K_COUPON_ISSUANCE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "K008", "쿠폰 발행에 실패했습니다."),
	K_COUPON_TYPE_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "K009", "이미 존재하는 쿠폰 타입 이름입니다."),
	K_COUPON_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "K010", "이미 존재하는 쿠폰 이름입니다."),
	K_COUPON_INVALID_TARGET(HttpStatus.BAD_REQUEST, "K011", "잘못된 쿠폰 타겟 등록 선택입니다."),

	// Review & Search Errors (리뷰/검색 오류) - R
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "리뷰를 찾을 수 없습니다."),
	REVIEW_ORDER_ITEM_NOT_PURCHASED(HttpStatus.BAD_REQUEST, "R002", "구매하지 않은 상품에 대한 리뷰는 작성할 수 없습니다."),
	REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "R003", "이미 작성된 리뷰가 존재합니다."), // 해당 주문 상품에 대해
	REVIEW_INVALID_STAR_RATING(HttpStatus.BAD_REQUEST, "R004", "유효하지 않은 별점입니다."),
	REVIEW_IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "R005", "리뷰 이미지 업로드에 실패했습니다."),
	REVIEW_CANNOT_DELETE(HttpStatus.METHOD_NOT_ALLOWED, "R006", "리뷰는 삭제할 수 없습니다. (수정만 가능)"),
	SEARCH_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "R007", "검색 엔진 실행 중 오류가 발생했습니다."),
	SEARCH_INVALID_SORT_CRITERIA(HttpStatus.BAD_REQUEST, "R008", "유효하지 않은 검색 정렬 기준입니다."),
	SEARCH_INVALID_WEIGHT_CONFIG(HttpStatus.INTERNAL_SERVER_ERROR, "R009", "검색 가중치 설정이 올바르지 않습니다."),

	// Delivery Errors (배송 오류) - D
	DELIVERY_RULE_CANNOT_DELETE_DEFAULT(HttpStatus.METHOD_NOT_ALLOWED, "D001", "기본 배송 정책은 삭제할 수 없습니다."),
	DELIVERY_RULE_NOT_FOUND(HttpStatus.NOT_FOUND, "D002", "해당 배송정책이 없습니다."),
	DEFAULT_DELIVERY_RULE_NOT_FOUND(HttpStatus.NOT_FOUND, "D003", "기본 배송 정책이 없습니다."),
	DELIVERY_RULE_ALREADY_EXISTS(HttpStatus.CONFLICT, "D004", "해당 이름의 배송정책이 존재합니다.");

	private final HttpStatus status; // HTTP 상태 코드
	private final String code;       // 고유 오류 코드 (클라이언트 사용)
	// 개발/디버깅 목적
	private final String message;    // 개발자를 위한 기본 메시지 (필수 X)

	ErrorCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	public static ErrorCode findByStringCode(String code) {
		for (ErrorCode errorCode : ErrorCode.values()) {
			if (errorCode.getCode().equals(code)) {
				return errorCode;
			}
		}
		return INTERNAL_SERVER_ERROR;
	}
}
