package myaimentor_api.user.account.dto;

import myaimentor_api.user.domain.Role;
import myaimentor_api.user.domain.User;

public record UserResponse(
		Long id,
		String email,
		String name,
		Role role
) {
	public static UserResponse from(User user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getRole());
	}
}
