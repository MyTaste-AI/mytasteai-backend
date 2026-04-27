package myaimentor_api.mentor.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AI 서비스 POST /knowledge/preview 요청 본문.
 * 청킹 파라미터를 직접 전달 — 봇 ID 가 아니라 settings 그대로 보냄 (재사용 편의).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record KnowledgePreviewRequest(
		String content,
		@JsonProperty("chunk_size") Integer chunkSize,
		@JsonProperty("chunk_overlap") Integer chunkOverlap,
		@JsonProperty("chunk_splitter") String chunkSplitter
) {
}
