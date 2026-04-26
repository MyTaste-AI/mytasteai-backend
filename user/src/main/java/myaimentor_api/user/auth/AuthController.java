package myaimentor_api.user.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myaimentor_api.common.auth.AuthPrincipal;
import myaimentor_api.common.error.BusinessException;
import myaimentor_api.common.error.ErrorCode;
import myaimentor_api.user.auth.dto.LoginRequest;
import myaimentor_api.user.auth.dto.MeResponse;
import myaimentor_api.user.auth.dto.SignupRequest;
import myaimentor_api.user.auth.dto.TokenResponse;
import myaimentor_api.user.domain.User;
import myaimentor_api.user.domain.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * 인증 API
 * - 회원가입 / 로그인 / 로그아웃 / 내 정보 조회를 담당.
 * - 토큰 발급은 JwtProvider, 검증은 common 의 JwtVerifier 가 책임.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final UserRepository userRepository;

	/**
	 * 회원가입
	 * POST /auth/signup
	 *
	 * 이메일/비밀번호/이름을 받아 새 사용자를 생성한다.
	 * - 성공 시 201 Created + Location 헤더(/users/{id})
	 * - 이메일 중복 시 409 Conflict (AUTH-003)
	 * - 검증 실패(빈 값/형식 위반) 시 400 Bad Request (SYS-001)
	 */
	@PostMapping("/signup")
	public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
		Long id = authService.signup(request);
		URI location = UriComponentsBuilder.fromPath("/users/{id}").buildAndExpand(id).toUri();
		return ResponseEntity.created(location).build();
	}

	/**
	 * 로그인 (JWT 발급)
	 * POST /auth/login
	 *
	 * 이메일/비밀번호 검증 후 access token 발급.
	 * - 성공 시 200 OK + { tokenType, accessToken, expiresIn }
	 * - 인증 실패 시 401 Unauthorized (AUTH-002) — 이메일/비번 불일치를 구분하지 않음(보안)
	 */
	@PostMapping("/login")
	public TokenResponse login(@RequestBody @Valid LoginRequest request) {
		return authService.login(request);
	}

	/**
	 * 로그아웃
	 * POST /auth/logout
	 *
	 * 현재는 stateless 방식 — 서버는 별도 처리 없이 204를 반환하고
	 * 클라이언트가 보유한 토큰을 폐기한다. (블랙리스트 정책은 추후 도입)
	 */
	@PostMapping("/logout")
	@org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout() {
	}

	/**
	 * 내 정보 조회
	 * GET /auth/me
	 *
	 * Authorization: Bearer {token} 헤더의 JWT 에서 사용자 ID 를 꺼내 본인 정보를 반환한다.
	 * - 토큰 누락/만료/위조 시 401 Unauthorized (AUTH-001)
	 * - 토큰의 userId 에 해당하는 사용자가 DB 에 없으면 401 (토큰은 유효하나 사용자 삭제됨)
	 */
	@GetMapping("/me")
	public MeResponse me(@AuthenticationPrincipal AuthPrincipal principal) {
		if (principal == null) {
			throw new BusinessException(ErrorCode.AUTH_REQUIRED);
		}
		User user = userRepository.findById(principal.userId())
				.orElseThrow(() -> new BusinessException(ErrorCode.AUTH_REQUIRED));
		return MeResponse.from(user);
	}
}
