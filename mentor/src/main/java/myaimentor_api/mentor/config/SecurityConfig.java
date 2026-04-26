package myaimentor_api.mentor.config;

import myaimentor_api.common.auth.JwtAuthenticationFilter;
import myaimentor_api.common.auth.JwtProperties;
import myaimentor_api.common.auth.JwtVerifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * mentor 서비스 보안 설정.
 * - GET 엔드포인트는 인증 사용자면 누구나 (USER 포함)
 * - 쓰기 엔드포인트(POST/PATCH/DELETE)는 ADMIN 만 허용
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter(JwtVerifier jwtVerifier) {
		return new JwtAuthenticationFilter(jwtVerifier);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(e -> e
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/error").permitAll()
						// 카테고리/봇 관리 — ADMIN
						.requestMatchers(HttpMethod.POST, "/categories", "/bots").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/categories/*", "/bots/*").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PATCH, "/bots/*").hasRole("ADMIN")
						// 지식 — ADMIN (등록/삭제만)
						.requestMatchers(HttpMethod.POST, "/bots/*/knowledge").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/bots/*/knowledge/*").hasRole("ADMIN")
						// 그 외 (조회 계열)는 인증만 되어 있으면 OK
						.anyRequest().authenticated()
				)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
