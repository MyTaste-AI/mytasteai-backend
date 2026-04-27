package myaimentor_api.common.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Authorization: Bearer {token} → SecurityContext 주입.
 * - 검증 실패 시 컨텍스트 비우고 통과 (인가는 SecurityFilterChain 에서 결정)
 *
 * @Component 가 아니므로 각 서비스의 SecurityConfig 에서 @Bean 으로 등록한다
 * (gateway 같은 reactive 모듈이 servlet 필터를 우연히 픽업하지 않도록).
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtVerifier jwtVerifier;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header != null && header.startsWith(BEARER_PREFIX)) {
			String token = header.substring(BEARER_PREFIX.length());
			try {
				AuthPrincipal principal = jwtVerifier.parse(token);
				var authority = new SimpleGrantedAuthority("ROLE_" + principal.role());
				var authentication = new UsernamePasswordAuthenticationToken(principal, null, List.of(authority));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (Exception e) {
				log.debug("JWT 검증 실패: {}", e.getMessage());
				SecurityContextHolder.clearContext();
			}
		}
		chain.doFilter(request, response);
	}
}
