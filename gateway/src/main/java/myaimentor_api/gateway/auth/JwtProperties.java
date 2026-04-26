package myaimentor_api.gateway.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 설정 — gateway 는 토큰 1차 검증만 수행 (다운스트림이 자체 검증).
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret) {
}
