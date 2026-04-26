package myaimentor_api.user.auth;

import lombok.RequiredArgsConstructor;
import myaimentor_api.common.auth.JwtProperties;
import myaimentor_api.common.error.BusinessException;
import myaimentor_api.common.error.ErrorCode;
import myaimentor_api.user.auth.dto.LoginRequest;
import myaimentor_api.user.auth.dto.SignupRequest;
import myaimentor_api.user.auth.dto.TokenResponse;
import myaimentor_api.user.domain.Role;
import myaimentor_api.user.domain.User;
import myaimentor_api.user.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스 구현.
 * - 회원가입: 이메일 중복 검사 → BCrypt 해시 → 사용자 저장 (기본 ROLE = USER)
 * - 로그인:   이메일 조회 → 비밀번호 일치 검증 → JwtProvider 로 access token 발급
 *
 * 비밀번호 평문은 Service 진입 시점부터 즉시 해시되며, DB 에는 해시만 저장된다.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;
	private final JwtProperties jwtProperties;

	@Override
	@Transactional
	public Long signup(SignupRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}
		User user = User.builder()
				.email(request.email())
				.password(passwordEncoder.encode(request.password()))
				.name(request.name())
				.role(Role.USER)
				.build();
		return userRepository.save(user).getId();
	}

	@Override
	@Transactional(readOnly = true)
	public TokenResponse login(LoginRequest request) {
		// 이메일 미존재와 비밀번호 불일치를 같은 메시지로 처리 — 사용자 존재 여부 노출 방지
		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));
		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
		}
		String accessToken = jwtProvider.createAccessToken(user);
		// expiresIn 은 초 단위 (JwtProperties 는 밀리초)
		return TokenResponse.bearer(accessToken, jwtProperties.accessTokenExpiration() / 1000);
	}
}
