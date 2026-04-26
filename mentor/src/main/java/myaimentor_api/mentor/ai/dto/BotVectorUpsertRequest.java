package myaimentor_api.mentor.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AI 서비스 POST /bot-vectors 요청 본문.
 * snake_case 키를 그대로 사용하기 위해 JsonProperty 로 매핑.
 */
public record BotVectorUpsertRequest(
		@JsonProperty("bot_id") Long botId,
		String description,
		String provider
) {
}
