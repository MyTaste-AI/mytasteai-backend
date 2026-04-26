package myaimentor_api.mentor.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 설정 바인딩 (jwt.* 프로퍼티).
 * mentor는 토큰 검증만 하므로 secret 만 실제로 사용한다.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
		String secret,
		long accessTokenExpiration,
		long refreshTokenExpiration
) {
}
