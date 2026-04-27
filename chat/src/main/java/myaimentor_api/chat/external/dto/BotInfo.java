package myaimentor_api.chat.external.dto;

/**
 * mentor 의 GET /bots/{id} 응답 중 chat 모듈이 실제로 쓰는 필드만 추림.
 * Jackson 의 FAIL_ON_UNKNOWN_PROPERTIES=false 기본 설정으로 나머지 필드는 무시됨.
 *
 * 검색 설정(searchType/topK/scoreThreshold) 은 봇 설정 → AI generate 호출 시 전달용.
 */
public record BotInfo(
		Long id,
		String name,
		String systemPrompt,
		String provider,
		String searchType,
		Integer topK,
		Double scoreThreshold
) {
}
