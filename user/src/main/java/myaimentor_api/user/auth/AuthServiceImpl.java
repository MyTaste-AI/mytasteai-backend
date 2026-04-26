package myaimentor_api.user.auth;

import lombok.RequiredArgsConstructor;
import myaimentor_api.user.auth.dto.LoginRequest;
import myaimentor_api.user.auth.dto.SignupRequest;
import myaimentor_api.user.auth.dto.TokenResponse;
import myaimentor_api.user.domain.Role;
import myaimentor_api.user.domain.User;
import myaimentor_api.user.domain.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
			throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
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
		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));
		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
		}
		String accessToken = jwtProvider.createAccessToken(user);
		return TokenResponse.bearer(accessToken, jwtProperties.accessTokenExpiration() / 1000);
	}
}
