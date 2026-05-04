package myaimentor_api.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 전 서비스 공통 에러 코드.
 * - status:  HTTP 응답 상태
 * - code:    클라이언트가 분기에 사용하는 식별자 (도메인-번호)
 * - message: 기본 사용자 메시지 (BusinessException 에서 override 가능)
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// ===== 공통 / 인증 =====
	AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH-001", "인증이 필요합니다."),
	INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH-002", "이메일 또는 비밀번호가 올바르지 않습니다."),
	EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH-003", "이미 사용 중인 이메일입니다."),

	// ===== 사용자 =====
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "사용자를 찾을 수 없습니다."),
	USER_WITHDRAWN(HttpStatus.FORBIDDEN, "USER-002", "탈퇴 처리된 계정입니다."),
	USER_ALREADY_WITHDRAWN(HttpStatus.CONFLICT, "USER-003", "이미 탈퇴 진행 중인 계정입니다."),

	// ===== 멘토 / 봇 =====
	BOT_NOT_FOUND(HttpStatus.NOT_FOUND, "BOT-001", "봇을 찾을 수 없습니다."),

	// ===== 카테고리 =====
	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CAT-001", "카테고리를 찾을 수 없습니다."),
	CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "CAT-002", "이미 존재하는 카테고리입니다."),
	CATEGORY_INVALID(HttpStatus.BAD_REQUEST, "CAT-003", "존재하지 않는 카테고리입니다."),
	CATEGORY_MAX_DEPTH(HttpStatus.BAD_REQUEST, "CAT-004", "카테고리는 최대 2단계(대분류/중분류) 까지만 만들 수 있습니다."),
	CATEGORY_HAS_CHILDREN(HttpStatus.CONFLICT, "CAT-005", "하위 카테고리가 있어 삭제할 수 없습니다. 먼저 하위 항목을 삭제해주세요."),

	// ===== 채팅 =====
	CHAT_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT-001", "세션을 찾을 수 없습니다."),

	// ===== 외부 서비스 (mentor / AI) =====
	MENTOR_BOT_FETCH_FAILED(HttpStatus.BAD_GATEWAY, "EXT-001", "mentor 봇 조회 실패"),
	AI_GENERATE_FAILED(HttpStatus.BAD_GATEWAY, "EXT-002", "AI generate 실패"),
	AI_BOT_VECTOR_UPSERT_FAILED(HttpStatus.BAD_GATEWAY, "EXT-003", "AI bot-vector upsert 실패"),
	AI_BOT_VECTOR_DELETE_FAILED(HttpStatus.BAD_GATEWAY, "EXT-004", "AI bot-vector 삭제 실패"),
	AI_KNOWLEDGE_CREATE_FAILED(HttpStatus.BAD_GATEWAY, "EXT-005", "AI knowledge 등록 실패"),
	AI_KNOWLEDGE_LIST_FAILED(HttpStatus.BAD_GATEWAY, "EXT-006", "AI knowledge 조회 실패"),
	AI_KNOWLEDGE_DELETE_FAILED(HttpStatus.BAD_GATEWAY, "EXT-007", "AI knowledge 삭제 실패"),
	AI_KNOWLEDGE_PREVIEW_FAILED(HttpStatus.BAD_GATEWAY, "EXT-008", "AI knowledge 미리보기 실패"),

	// ===== 시스템 =====
	VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "SYS-001", "요청 값이 올바르지 않습니다."),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYS-002", "서버 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
