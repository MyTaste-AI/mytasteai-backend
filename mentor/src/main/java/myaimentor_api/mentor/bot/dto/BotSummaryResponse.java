package myaimentor_api.mentor.bot.dto;

import myaimentor_api.mentor.domain.Bot;
import myaimentor_api.mentor.domain.Provider;

/**
 * 봇 목록 응답용 — systemPrompt 같은 무거운 텍스트는 생략.
 */
public record BotSummaryResponse(
		Long id,
		String name,
		String description,
		Provider provider,
		Long categoryId,
		Boolean isPublic
) {
	public static BotSummaryResponse from(Bot b) {
		return new BotSummaryResponse(b.getId(), b.getName(), b.getDescription(), b.getProvider(), b.getCategoryId(), b.isPublic());
	}
}
