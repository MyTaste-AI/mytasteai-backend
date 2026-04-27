package myaimentor_api.mentor.bot.dto;

import myaimentor_api.mentor.domain.Bot;
import myaimentor_api.mentor.domain.ChunkSplitter;
import myaimentor_api.mentor.domain.Provider;
import myaimentor_api.mentor.domain.SearchType;

import java.time.Instant;

public record BotResponse(
		Long id,
		String name,
		String description,
		String systemPrompt,
		Provider provider,
		Long categoryId,
		Long createdBy,
		Boolean isPublic,
		// 검색 설정
		SearchType searchType,
		Integer topK,
		Double scoreThreshold,
		// 청킹 설정
		Integer chunkSize,
		Integer chunkOverlap,
		ChunkSplitter chunkSplitter,
		Instant createdAt,
		Instant updatedAt
) {
	public static BotResponse from(Bot b) {
		return new BotResponse(
				b.getId(), b.getName(), b.getDescription(), b.getSystemPrompt(),
				b.getProvider(), b.getCategoryId(), b.getCreatedBy(), b.isPublic(),
				b.getSearchType(), b.getTopK(), b.getScoreThreshold(),
				b.getChunkSize(), b.getChunkOverlap(), b.getChunkSplitter(),
				b.getCreatedAt(), b.getUpdatedAt()
		);
	}
}
