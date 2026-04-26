package myaimentor_api.user.auth;

import myaimentor_api.user.domain.Role;

public record AuthPrincipal(Long userId, String email, Role role) {
}
