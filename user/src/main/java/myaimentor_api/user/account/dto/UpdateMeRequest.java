package myaimentor_api.user.account.dto;

import jakarta.validation.constraints.Size;

public record UpdateMeRequest(
		@Size(min = 1, max = 100) String name
) {
}
