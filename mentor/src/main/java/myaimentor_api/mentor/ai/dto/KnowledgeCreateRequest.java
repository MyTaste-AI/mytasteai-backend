package myaimentor_api.mentor.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AI 서비스 POST /knowledge 요청 본문.
 * 봇 설정의 청킹 파라미터(chunk_size/overlap/splitter) 도 함께 전달 →
 * AI 서비스가 이 값으로 텍스트를 분할해 임베딩 생성.
 * AI 서비스가 미지원이면 무시 가능 (NON_NULL 직렬화로 누락된 값은 보내지 않음).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record KnowledgeCreateRequest(
		@JsonProperty("bot_id") Long botId,
		String content,
		String provider,
		@JsonProperty("chunk_size") Integer chunkSize,
		@JsonProperty("chunk_overlap") Integer chunkOverlap,
		@JsonProperty("chunk_splitter") String chunkSplitter
) {
}
