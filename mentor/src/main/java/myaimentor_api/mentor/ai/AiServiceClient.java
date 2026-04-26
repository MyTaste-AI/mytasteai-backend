package myaimentor_api.mentor.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaimentor_api.mentor.ai.dto.BotVectorUpsertRequest;
import myaimentor_api.mentor.ai.dto.KnowledgeCreateRequest;
import myaimentor_api.mentor.ai.dto.KnowledgeResponse;
import myaimentor_api.mentor.domain.Provider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * AI 마이크로서비스 호출 어댑터.
 * - bot-vectors: 봇 라우팅 임베딩 upsert/delete
 * - knowledge:   봇 지식 등록/조회/삭제 (Spring DB에는 저장하지 않고 그대로 위임)
 *
 * 모든 호출은 동기(.block())로 처리한다 — mentor 모듈은 servlet 기반이며
 * 컨트롤러까지 reactor 타입을 노출하지 않는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiServiceClient {

	private final WebClient aiServiceWebClient;

	/* ===== Bot vectors ===== */

	public void upsertBotVector(Long botId, String description, Provider provider) {
		var body = new BotVectorUpsertRequest(botId, description, provider.toAiServiceValue());
		try {
			aiServiceWebClient.post()
					.uri("/bot-vectors")
					.bodyValue(body)
					.retrieve()
					.toBodilessEntity()
					.block();
		} catch (WebClientResponseException e) {
			throw translate(e, "AI bot-vector upsert 실패");
		}
	}

	public void deleteBotVector(Long botId, Provider provider) {
		try {
			aiServiceWebClient.delete()
					.uri(uri -> uri.path("/bot-vectors/{botId}")
							.queryParam("provider", provider.toAiServiceValue())
							.build(botId))
					.retrieve()
					.toBodilessEntity()
					.block();
		} catch (WebClientResponseException.NotFound ignored) {
			// AI 측에 없는 경우 — 멱등 삭제로 본다.
			log.debug("AI bot-vector 이미 없음. botId={}, provider={}", botId, provider);
		} catch (WebClientResponseException e) {
			throw translate(e, "AI bot-vector 삭제 실패");
		}
	}

	/* ===== Knowledge ===== */

	public KnowledgeResponse createKnowledge(Long botId, String content, Provider provider) {
		var body = new KnowledgeCreateRequest(botId, content, provider.toAiServiceValue());
		try {
			return aiServiceWebClient.post()
					.uri("/knowledge")
					.bodyValue(body)
					.retrieve()
					.bodyToMono(KnowledgeResponse.class)
					.block();
		} catch (WebClientResponseException e) {
			throw translate(e, "AI knowledge 등록 실패");
		}
	}

	public List<KnowledgeResponse> listKnowledge(Long botId, Provider provider, int limit, int offset) {
		try {
			return aiServiceWebClient.get()
					.uri(uri -> uri.path("/bots/{botId}/knowledge")
							.queryParam("provider", provider.toAiServiceValue())
							.queryParam("limit", limit)
							.queryParam("offset", offset)
							.build(botId))
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<KnowledgeResponse>>() {})
					.block();
		} catch (WebClientResponseException e) {
			throw translate(e, "AI knowledge 조회 실패");
		}
	}

	public void deleteKnowledge(Long knowledgeId, Provider provider) {
		try {
			aiServiceWebClient.delete()
					.uri(uri -> uri.path("/knowledge/{id}")
							.queryParam("provider", provider.toAiServiceValue())
							.build(knowledgeId))
					.retrieve()
					.toBodilessEntity()
					.block();
		} catch (WebClientResponseException e) {
			throw translate(e, "AI knowledge 삭제 실패");
		}
	}

	private ResponseStatusException translate(WebClientResponseException e, String defaultMessage) {
		HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
		log.warn("{} - status={}, body={}", defaultMessage, e.getStatusCode(), e.getResponseBodyAsString());
		// 4xx는 그대로 전파, 5xx는 502 Bad Gateway 로 변환
		if (status != null && status.is4xxClientError()) {
			return new ResponseStatusException(status, defaultMessage);
		}
		return new ResponseStatusException(HttpStatus.BAD_GATEWAY, defaultMessage);
	}
}
