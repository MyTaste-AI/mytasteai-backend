package myaimentor_api.chat.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * POST /chat 요청.
 * - sessionId 가 있으면 기존 세션에 이어서 대화, 없으면 새 세션 생성
 * - botId 는 필수 (어떤 봇에게 질문하는지)
 */
public record ChatRequest(
		String sessionId,
		@NotNull @Positive Long botId,
		@NotBlank String question
) {
}
