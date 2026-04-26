package myaimentor_api.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 예외. ErrorCode 를 기준으로 응답이 만들어진다.
 * 외부 서비스 호출처럼 status 가 동적으로 결정되는 경우 statusOverride 로 4xx 상태를 보존할 수 있다.
 */
@Getter
public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;
	private final HttpStatus statusOverride;

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.statusOverride = null;
	}

	public BusinessException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		this.statusOverride = null;
	}

	public BusinessException(ErrorCode errorCode, HttpStatus statusOverride) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.statusOverride = statusOverride;
	}

	public HttpStatus getStatus() {
		return statusOverride != null ? statusOverride : errorCode.getStatus();
	}
}
