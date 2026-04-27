package myaimentor_api.common.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 전역 예외 처리 — 모든 컨트롤러 응답이 ErrorResponse 형식으로 통일된다.
 *
 * 처리 우선순위:
 *   1) BusinessException        : ErrorCode 기반 응답
 *   2) 검증/바인딩 실패          : 400 + 필드별 상세
 *   3) ResponseStatusException  : 레거시 fallback
 *   4) Exception                : 500 INTERNAL_ERROR
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e, HttpServletRequest req) {
		log.debug("BusinessException: {} - {}", e.getErrorCode().getCode(), e.getMessage());
		return ResponseEntity.status(e.getStatus())
				.body(ErrorResponse.of(e.getErrorCode(), e.getMessage(), req.getRequestURI()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest req) {
		List<ErrorResponse.FieldError> fields = e.getBindingResult().getFieldErrors().stream()
				.map(f -> new ErrorResponse.FieldError(f.getField(), f.getRejectedValue(), f.getDefaultMessage()))
				.toList();
		return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus())
				.body(ErrorResponse.of(ErrorCode.VALIDATION_FAILED, req.getRequestURI(), fields));
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorResponse> handleBind(BindException e, HttpServletRequest req) {
		List<ErrorResponse.FieldError> fields = e.getBindingResult().getFieldErrors().stream()
				.map(f -> new ErrorResponse.FieldError(f.getField(), f.getRejectedValue(), f.getDefaultMessage()))
				.toList();
		return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus())
				.body(ErrorResponse.of(ErrorCode.VALIDATION_FAILED, req.getRequestURI(), fields));
	}

	@ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
	public ResponseEntity<ErrorResponse> handleBadRequest(Exception e, HttpServletRequest req) {
		log.debug("BadRequest: {}", e.getMessage());
		return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus())
				.body(ErrorResponse.of(ErrorCode.VALIDATION_FAILED, e.getMessage(), req.getRequestURI()));
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException e, HttpServletRequest req) {
		ErrorCode fallback = e.getStatusCode().is5xxServerError()
				? ErrorCode.INTERNAL_ERROR
				: ErrorCode.VALIDATION_FAILED;
		String msg = e.getReason() != null ? e.getReason() : fallback.getMessage();
		return ResponseEntity.status(e.getStatusCode())
				.body(ErrorResponse.of(fallback, msg, req.getRequestURI()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnknown(Exception e, HttpServletRequest req) {
		log.error("Unhandled exception", e);
		return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus())
				.body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, req.getRequestURI()));
	}
}
