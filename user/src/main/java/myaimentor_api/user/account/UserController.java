package myaimentor_api.user.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myaimentor_api.common.auth.AuthPrincipal;
import myaimentor_api.common.error.BusinessException;
import myaimentor_api.common.error.ErrorCode;
import myaimentor_api.user.account.dto.UpdateMeRequest;
import myaimentor_api.user.account.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 API
 * - 사용자 단건 조회 및 본인 정보 수정 담당.
 * - 인증된 사용자만 접근 가능 (SecurityConfig 에서 보호).
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	/**
	 * 사용자 목록 조회 (ADMIN 전용)
	 * GET /users?query=...&page=...&size=...
	 *
	 * 봇 접근권 관리 화면에서 사용자 픽커 용도. query 가 있으면 이메일/이름 부분일치 검색.
	 */
	@GetMapping
	public Page<UserResponse> findAll(
			@RequestParam(required = false) String query,
			@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
	) {
		return userService.findAll(query, pageable);
	}

	/**
	 * 사용자 단건 조회
	 * GET /users/{id}
	 *
	 * 공개 정보(id, email, name, role)만 반환한다. 비밀번호는 절대 노출하지 않음.
	 * - 존재하지 않는 ID 일 경우 404 Not Found (USER-001)
	 */
	@GetMapping("/{id}")
	public UserResponse findById(@PathVariable Long id) {
		return userService.findById(id);
	}

	/**
	 * 내 정보 수정
	 * PATCH /users/me
	 *
	 * 인증된 사용자가 본인의 프로필(현재는 name)을 부분 업데이트한다.
	 * - 토큰 누락/만료 시 401 Unauthorized (AUTH-001)
	 * - 빈 값/길이 위반 시 400 Bad Request (SYS-001)
	 */
	@PatchMapping("/me")
	public UserResponse updateMe(
			@AuthenticationPrincipal AuthPrincipal principal,
			@RequestBody @Valid UpdateMeRequest request
	) {
		if (principal == null) {
			throw new BusinessException(ErrorCode.AUTH_REQUIRED);
		}
		return userService.updateMe(principal.userId(), request);
	}
}
