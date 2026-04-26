package myaimentor_api.user.auth;

import myaimentor_api.user.auth.dto.LoginRequest;
import myaimentor_api.user.auth.dto.SignupRequest;
import myaimentor_api.user.auth.dto.TokenResponse;

/**
 * 인증 서비스
 * - 회원가입과 로그인(JWT 발급) 비즈니스 로직 정의.
 */
public interface AuthService {

	/**
	 * 회원가입: 이메일 중복 검사 후 BCrypt 해시 비밀번호로 사용자 저장.
	 *
	 * @return 생성된 사용자 ID
	 */
	Long signup(SignupRequest request);

	/**
	 * 로그인: 이메일/비밀번호 검증 후 access token 발급.
	 */
	TokenResponse login(LoginRequest request);
}
