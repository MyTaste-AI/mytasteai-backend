package myaimentor_api.chat.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 설정 바인딩 (jwt.* 프로퍼티). chat 서비스는 토큰 검증만 수행.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
		String secret,
		long accessTokenExpiration,
		long refreshTokenExpiration
) {
}
