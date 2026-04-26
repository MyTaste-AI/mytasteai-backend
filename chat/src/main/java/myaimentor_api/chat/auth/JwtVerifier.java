package myaimentor_api.chat.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT 검증 전용 — 발급은 user 모듈에서 담당.
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
}
