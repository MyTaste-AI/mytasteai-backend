package myaimentor_api.chat.external.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AI 서비스 POST /generate 요청 본문.
          * - bot_id / system_prompt 는 nullable — 클라이언트가 봇을 지정하지 않은 자동 라우팅 모드도 지원.
 * - search_type / top_k / score_threshold 는 봇 설정에서 가져온 RAG 검색 파라미터.
 *   AI 서비스가 아직 키워드/하이브리드 미지원이면 무시 가능.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenerateRequest(
		String question,
		String provider,
		@JsonProperty("bot_id") Long botId,
		@JsonProperty("system_prompt") String systemPrompt,
		@JsonProperty("search_type") String searchType,
		@JsonProperty("top_k") Integer topK,
		@JsonProperty("score_threshold") Double scoreThreshold
) {
}
