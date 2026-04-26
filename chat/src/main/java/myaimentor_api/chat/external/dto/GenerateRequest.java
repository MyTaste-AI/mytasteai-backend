package myaimentor_api.chat.external.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AI 서비스 POST /generate 요청 본문.
 * bot_id / system_prompt 는 nullable — 클라이언트가 봇을 지정하지 않은 자동 라우팅 모드도 지원.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenerateRequest(
		String question,
		String provider,
		@JsonProperty("bot_id") Long botId,
		@JsonProperty("system_prompt") String systemPrompt,
		@JsonProperty("top_k") Integer topK
) {
}
