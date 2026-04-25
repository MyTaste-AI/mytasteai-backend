package myaimentor_api.user.auth.dto;

import myaimentor_api.user.domain.Role;
import myaimentor_api.user.domain.User;

import java.time.Instant;

public record MeResponse(
		Long id,
		String email,
		String name,
		Role role,
		Instant createdAt
) {
	public static MeResponse from(User user) {
		return new MeResponse(user.getId(), user.getEmail(), user.getName(), user.getRole(), user.getCreatedAt());
	}
}
