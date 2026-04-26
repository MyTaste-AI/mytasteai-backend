package myaimentor_api.user.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myaimentor_api.user.account.dto.UpdateMeRequest;
import myaimentor_api.user.account.dto.UserResponse;
import myaimentor_api.user.auth.AuthPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * 사용자 API
 * - 사용자 단건 조회 및 본인 정보 수정 담당.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	/**
	 * 사용자 단건 조회
	 * GET /users/{id}
	 *
	 * 공개 정보(id, email, name, role)만 반환. 비밀번호는 절대 노출하지 않는다.
	 * - 존재하지 않는 ID일 경우 404 Not Found
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
	 * - 토큰 누락 시 401 Unauthorized
	 * - 빈 값/길이 위반 시 400 Bad Request
	 */
	@PatchMapping("/me")
	public UserResponse updateMe(
			@AuthenticationPrincipal AuthPrincipal principal,
			@RequestBody @Valid UpdateMeRequest request
	) {
		if (principal == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		return userService.updateMe(principal.userId(), request);
	}
}
