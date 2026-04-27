package myaimentor_api.mentor.bot.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BotAccessRequest(
		@NotNull @Positive Long userId
) {
}
