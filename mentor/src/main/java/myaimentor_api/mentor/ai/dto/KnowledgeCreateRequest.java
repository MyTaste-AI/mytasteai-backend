package myaimentor_api.mentor.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AI 서비스 POST /knowledge 요청 본문.
 */
public record KnowledgeCreateRequest(
		@JsonProperty("bot_id") Long botId,
		String content,
		String provider
) {
}
