package myaimentor_api.mentor.bot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import myaimentor_api.mentor.domain.Provider;

public record BotCreateRequest(
		@NotBlank @Size(max = 100) String name,
		@NotBlank String description,
		@NotBlank String systemPrompt,
		@NotNull Provider provider,
		@NotNull @Positive Long categoryId
) {
}
