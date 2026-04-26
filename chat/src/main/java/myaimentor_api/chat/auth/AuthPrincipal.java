package myaimentor_api.chat.auth;

/**
 * 인증된 사용자 컨텍스트. user 모듈의 Role enum을 의존하지 않도록 role 은 문자열 보관.
 */
public record AuthPrincipal(Long userId, String email, String role) {
}
