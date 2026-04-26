package myaimentor_api.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * 통일된 에러 응답 스키마.
 * - code:      ErrorCode.code (예: "AUTH-001")
 * - message:   사용자 메시지
 * - timestamp: 응답 시각 (UTC)
 * - path:      요청 경로 (디버깅용)
 * - errors:    검증 실패 시 필드별 상세 (그 외엔 생략)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
		String code,
		String message,
		Instant timestamp,
		String path,
		List<FieldError> errors
) {

	public static ErrorResponse of(ErrorCode code, String path) {
		return new ErrorResponse(code.getCode(), code.getMessage(), Instant.now(), path, null);
	}

	public static ErrorResponse of(ErrorCode code, String message, String path) {
		return new ErrorResponse(code.getCode(), message, Instant.now(), path, null);
	}

	public static ErrorResponse of(ErrorCode code, String path, List<FieldError> errors) {
		return new ErrorResponse(code.getCode(), code.getMessage(), Instant.now(), path, errors);
	}

	public record FieldError(String field, Object rejectedValue, String reason) {
	}
}
