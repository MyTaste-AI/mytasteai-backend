package myaimentor_api.gateway.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Gateway 1차 JWT 검증 필터.
 * - public path (회원가입/로그인/로그아웃) 는 통과
 * - 그 외 path 는 Authorization: Bearer 토큰 필수, 서명/만료 검증
 * - 통과한 요청은 헤더 그대로 다운스트림으로 forward → 다운스트림이 또 검증 (depth-in-defense)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter, Ordered {

	private static final String BEARER_PREFIX = "Bearer ";

	/** 토큰 없이 통과 가능한 public endpoint. */
	private static final Set<String> PUBLIC_PATHS = Set.of(
			"/auth/signup",
			"/auth/login",
			"/auth/logout"
	);

	private final JwtVerifier jwtVerifier;

	@Override
	public int getOrder() {
		// Gateway routing 보다 앞서 실행되도록 가장 높은 우선순위.
		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		// CORS preflight 는 무조건 통과.
		if (HttpMethod.OPTIONS.equals(request.getMethod())) {
			return chain.filter(exchange);
		}

		String path = request.getPath().value();
		if (PUBLIC_PATHS.contains(path)) {
			return chain.filter(exchange);
		}

		String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (header == null || !header.startsWith(BEARER_PREFIX)) {
			return reject(exchange, "missing bearer token");
		}

		String token = header.substring(BEARER_PREFIX.length());
		if (!jwtVerifier.isValid(token)) {
			return reject(exchange, "invalid token");
		}
		return chain.filter(exchange);
	}

	private Mono<Void> reject(ServerWebExchange exchange, String reason) {
		log.debug("gateway 401: {} ({})", exchange.getRequest().getPath().value(), reason);
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().setComplete();
	}
}
