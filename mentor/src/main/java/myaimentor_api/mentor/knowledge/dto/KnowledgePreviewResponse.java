package myaimentor_api.mentor.knowledge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 청킹 미리보기 응답 — AI 서비스가 반환한 구조 그대로.
 */
public record KnowledgePreviewResponse(
		List<Chunk> chunks,
		@JsonProperty("chunk_size") Integer chunkSize,
		@JsonProperty("chunk_overlap") Integer chunkOverlap,
		String splitter
) {
	public record Chunk(int index, String text) {
	}
}
