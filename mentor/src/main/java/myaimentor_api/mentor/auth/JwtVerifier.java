package myaimentor_api.mentor.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT 검증 전용 컴포넌트.
 * mentor는 토큰 발급은 하지 않고 (user 모듈 담당), 받은 토큰을 파싱해 사용자 정보를 꺼낸다.
 */
@Component
public class JwtVerifier {

	private static final String CLAIM_EMAIL = "email";
	private static final String CLAIM_ROLE = "role";

	private final SecretKey key;

	public JwtVerifier(JwtProperties properties) {
		this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 서명 검증 + claim 추출. 검증 실패 시 JJWT 예외가 그대로 던져진다 (필터에서 catch).
	 */
	public AuthPrincipal parse(String token) {
		Jws<Claims> jws = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token);
		Claims claims = jws.getPayload();
		Long userId = Long.parseLong(claims.getSubject());
		String email = claims.get(CLAIM_EMAIL, String.class);
		String role = claims.get(CLAIM_ROLE, String.class);
		return new AuthPrincipal(userId, email, role);
	}
}
