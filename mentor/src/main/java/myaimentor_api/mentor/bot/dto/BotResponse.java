package myaimentor_api.mentor.bot.dto;

import myaimentor_api.mentor.domain.Bot;
import myaimentor_api.mentor.domain.Provider;

import java.time.Instant;

public record BotResponse(
		Long id,
		String name,
		String description,
		String systemPrompt,
		Provider provider,
		Long categoryId,
		Long createdBy,
		Instant createdAt,
		Instant updatedAt
) {
	public static BotResponse from(Bot b) {
		return new BotResponse(
				b.getId(), b.getName(), b.getDescription(), b.getSystemPrompt(),
				b.getProvider(), b.getCategoryId(), b.getCreatedBy(),
				b.getCreatedAt(), b.getUpdatedAt()
		);
	}
}
