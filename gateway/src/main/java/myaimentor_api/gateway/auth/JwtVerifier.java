package myaimentor_api.gateway.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT 서명 검증 — payload 추출은 다운스트림에서 다시 함.
 * gateway 는 "올바른 토큰인지"만 빠르게 가린다.
 */
@Component
public class JwtVerifier {

	private final SecretKey key;

	public JwtVerifier(JwtProperties properties) {
		this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 토큰 유효 여부만 반환. 만료/서명 위반 시 false.
	 */
	public boolean isValid(String token) {
		try {
			Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
