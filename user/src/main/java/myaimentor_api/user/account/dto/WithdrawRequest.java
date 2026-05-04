package myaimentor_api.user.account.dto;

import jakarta.validation.constraints.Size;

/**
 * 회원 탈퇴 요청. 탈퇴 사유는 선택 입력 (서비스 개선용 통계).
 * 길이 제한은 운영 통계 분석에 무리 없는 수준으로 잡아둔다.
 */
public record WithdrawRequest(
		@Size(max = 500) String reason
) {
}
