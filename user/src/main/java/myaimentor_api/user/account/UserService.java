package myaimentor_api.user.account;

import myaimentor_api.user.account.dto.UpdateMeRequest;
import myaimentor_api.user.account.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
	 * 사용자 목록 (ADMIN 픽커용) — 이메일/이름 부분일치 검색 지원.
	 * @param query null/blank 이면 전체 목록
	 */
	Page<UserResponse> findAll(String query, Pageable pageable);

	/**
	 * 본인 프로필 부분 업데이트 (현재는 name만 지원).
	 */
	UserResponse updateMe(Long userId, UpdateMeRequest request);
}
