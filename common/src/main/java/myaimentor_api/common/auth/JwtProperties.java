package myaimentor_api.common.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * JWT 설정 바인딩 (jwt.* 프로퍼티).
 * - secret: 서명 키 (전 서비스 공용)
 * - access/refresh expiration: 발급 측 (user 모듈) 만 사용. 검증/게이트웨이는 0 default 허용.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
		String secret,
		@DefaultValue("0") long accessTokenExpiration,
		@DefaultValue("0") long refreshTokenExpiration
) {
}
