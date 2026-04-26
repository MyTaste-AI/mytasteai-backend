package myaimentor_api.mentor.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

/**
 * AI 서비스의 KnowledgeResponse 그대로 받는 DTO.
 * Spring 측에서는 별도 저장 없이 클라이언트에 그대로 전달(위임형).
 */
public record KnowledgeResponse(
		Long id,
		@JsonProperty("bot_id") Long botId,
		String content,
		String provider,
		@JsonProperty("has_embedding") boolean hasEmbedding,
		@JsonProperty("created_at") OffsetDateTime createdAt
) {
}
