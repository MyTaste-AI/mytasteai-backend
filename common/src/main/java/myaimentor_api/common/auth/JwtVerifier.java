package myaimentor_api.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT 검증 전용 (발급은 user 모듈의 JwtProvider 가 담당).
 * - parse:    서명 검증 + claim 추출 → AuthPrincipal
 * - isValid:  서명/만료 여부만 빠르게 판정 (gateway 1차 검증용)
 */
@Component
public class JwtVerifier {

	private static final String CLAIM_EMAIL = "email";
	private static final String CLAIM_ROLE = "role";

	private final SecretKey key;

	public JwtVerifier(JwtProperties properties) {
		this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
	}

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

	public boolean isValid(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
