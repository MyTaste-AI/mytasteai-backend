package myaimentor_api.chat.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * AI 서비스 /generate 응답.
 */
public record GenerateResponse(
		String question,
		String provider,
		@JsonProperty("selected_bot_id") Long selectedBotId,
		List<RetrievedKnowledge> retrieved,
		String answer,
		String mode
) {
	public record RetrievedKnowledge(
			Long id,
			String content,
			Double distance
	) {
	}
}
