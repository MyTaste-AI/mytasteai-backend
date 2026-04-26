package myaimentor_api.chat.api.dto;

/**
 * POST /chat 응답.
 * - sessionId: 새로 만들어졌거나 이어진 세션의 ID
 * - answer:    LLM 답변
 * - botId:     실제로 답변을 만든 봇 (요청 botId 와 동일)
 * - mode:      AI 서비스가 알려주는 동작 모드 ('dummy' 또는 provider 명)
 */
public record ChatResponse(
		String sessionId,
		Long botId,
		String answer,
		String mode
) {
}
