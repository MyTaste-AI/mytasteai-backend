package myaimentor_api.mentor.bot.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import myaimentor_api.mentor.domain.ChunkSplitter;
import myaimentor_api.mentor.domain.Provider;
import myaimentor_api.mentor.domain.SearchType;

/**
 * 봇 부분 수정 요청. 모든 필드 nullable — null 인 필드는 변경 안 함.
 */
public record BotUpdateRequest(
		@Size(min = 1, max = 100) String name,
		String description,
		String systemPrompt,
		Provider provider,
		@Positive Long categoryId,

		// 검색 설정
		SearchType searchType,
		@Min(1) @Max(20) Integer topK,
		@DecimalMin("0.0") @DecimalMax("1.0") Double scoreThreshold,

		// 청킹 설정
		@Min(100) @Max(2000) Integer chunkSize,
		@PositiveOrZero @Max(500) Integer chunkOverlap,
		ChunkSplitter chunkSplitter
) {
}
