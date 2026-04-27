package myaimentor_api.mentor.bot.dto;

import myaimentor_api.mentor.domain.BotAccess;

import java.time.Instant;

public record BotAccessResponse(
		Long userId,
		Instant createdAt
) {
	public static BotAccessResponse from(BotAccess a) {
		return new BotAccessResponse(a.getUserId(), a.getCreatedAt());
	}
}
