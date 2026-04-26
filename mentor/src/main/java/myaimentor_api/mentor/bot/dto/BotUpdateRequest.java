package myaimentor_api.mentor.bot.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import myaimentor_api.mentor.domain.Provider;

/**
 * 봇 부분 수정 요청. 모든 필드 nullable — null 인 필드는 변경 안 함.
 */
public record BotUpdateRequest(
		@Size(min = 1, max = 100) String name,
		String description,
		String systemPrompt,
		Provider provider,
		@Positive Long categoryId
) {
}
