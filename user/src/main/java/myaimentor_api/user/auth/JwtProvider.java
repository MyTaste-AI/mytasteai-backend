package myaimentor_api.user.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import myaimentor_api.user.domain.Role;
import myaimentor_api.user.domain.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {

	private static final String CLAIM_EMAIL = "email";
	private static final String CLAIM_ROLE = "role";

	private final SecretKey key;
	private final long accessTokenExpiration;

	public JwtProvider(JwtProperties properties) {
		this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
		this.accessTokenExpiration = properties.accessTokenExpiration();
	}

	public String createAccessToken(User user) {
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(String.valueOf(user.getId()))
				.claim(CLAIM_EMAIL, user.getEmail())
				.claim(CLAIM_ROLE, user.getRole().name())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusMillis(accessTokenExpiration)))
				.signWith(key)
				.compact();
	}

	public AuthPrincipal parse(String token) {
		Jws<Claims> jws = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token);
		Claims claims = jws.getPayload();
		Long userId = Long.parseLong(claims.getSubject());
		String email = claims.get(CLAIM_EMAIL, String.class);
		Role role = Role.valueOf(claims.get(CLAIM_ROLE, String.class));
		return new AuthPrincipal(userId, email, role);
	}
}
