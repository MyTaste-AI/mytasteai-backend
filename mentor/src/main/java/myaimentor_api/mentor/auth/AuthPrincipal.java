package myaimentor_api.mentor.auth;

/**
 * JWT 검증 후 SecurityContext에 저장되는 인증 주체.
 * - userId: 토큰의 sub claim
 * - email:  토큰의 email claim
 * - role:   "USER" 또는 "ADMIN" (문자열로 보관 — mentor는 user 모듈의 Role enum을 의존하지 않음)
 */
public record AuthPrincipal(Long userId, String email, String role) {
}
