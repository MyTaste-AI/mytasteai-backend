package myaimentor_api.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * 브라우저 → gateway CORS 허용.
 *
 * - 개발 편의를 위해 localhost 의 모든 포트 허용 (5173 vite, 5174 등)
 * - JwtAuthenticationWebFilter 보다 먼저 실행되어야 401 응답에도 CORS 헤더가 붙는다
 *   → @Order(HIGHEST_PRECEDENCE) + Jwt 필터는 HIGHEST_PRECEDENCE+1
 *
 * 운영 환경에서는 allowedOriginPatterns 를 실제 도메인으로 좁혀야 한다.
 */
@Configuration
public class CorsConfig {

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration config = new CorsConfiguration();
		// localhost 전 포트 허용 (dev). credentials 와 함께 쓰려면 allowedOrigins 가 아닌 patterns 사용.
		config.setAllowedOriginPatterns(List.of("http://localhost:*"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		// 회원가입 응답의 Location, JWT 응답 헤더 등 클라이언트가 읽을 수 있게 노출
		config.setExposedHeaders(List.of("Location", "Authorization"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsWebFilter(source);
	}
}
