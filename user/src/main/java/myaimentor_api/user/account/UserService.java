package myaimentor_api.user.account;

import myaimentor_api.user.account.dto.UpdateMeRequest;
import myaimentor_api.user.account.dto.UserResponse;

/**
 * 사용자 서비스
 * - 사용자 정보 조회 및 본인 프로필 수정 비즈니스 로직 정의.
 */
public interface UserService {

	/**
	 * 사용자 단건 조회 (공개 필드만 반환).
	 */
	UserResponse findById(Long id);

	/**
	 * 본인 프로필 부분 업데이트 (현재는 name만 지원).
	 */
	UserResponse updateMe(Long userId, UpdateMeRequest request);
}
