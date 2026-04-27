package myaimentor_api.common.auth;

/**
 * 인증된 사용자 컨텍스트.
 * - userId: 토큰의 sub claim (Long)
 * - email:  토큰의 email claim
 * - role:   "USER" / "ADMIN" — 모듈간 결합을 피하기 위해 enum 이 아닌 문자열로 보관
 */
public record AuthPrincipal(Long userId, String email, String role) {
}
