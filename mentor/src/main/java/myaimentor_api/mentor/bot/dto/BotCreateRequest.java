package myaimentor_api.mentor.bot.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import myaimentor_api.mentor.domain.ChunkSplitter;
import myaimentor_api.mentor.domain.Provider;
import myaimentor_api.mentor.domain.SearchType;

/**
 * 봇 생성 요청.
 * 검색 설정/청킹 설정은 옵셔널 — 비우면 Bot 의 기본값 사용.
 */
public record BotCreateRequest(
		@NotBlank @Size(max = 100) String name,
		@NotBlank String description,
		@NotBlank String systemPrompt,
		@NotNull Provider provider,
		@NotNull @Positive Long categoryId,

		// 검색 설정 (옵션)
		SearchType searchType,
		@Min(1) @Max(20) Integer topK,
		@DecimalMin("0.0") @DecimalMax("1.0") Double scoreThreshold,

		// 청킹 설정 (옵션)
		@Min(100) @Max(2000) Integer chunkSize,
		@PositiveOrZero @Max(500) Integer chunkOverlap,
		ChunkSplitter chunkSplitter,

		// 접근권 — 미지정 시 공개(true)
		Boolean isPublic
) {
}
