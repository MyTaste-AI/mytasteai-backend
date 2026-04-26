package myaimentor_api.mentor.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaimentor_api.common.error.BusinessException;
import myaimentor_api.common.error.ErrorCode;
import myaimentor_api.mentor.ai.dto.BotVectorUpsertRequest;
import myaimentor_api.mentor.ai.dto.KnowledgeCreateRequest;
import myaimentor_api.mentor.ai.dto.KnowledgePreviewRequest;
import myaimentor_api.mentor.ai.dto.KnowledgeResponse;
import myaimentor_api.mentor.domain.ChunkSplitter;
import myaimentor_api.mentor.domain.Provider;
import myaimentor_api.mentor.knowledge.dto.KnowledgePreviewResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
			throw translate(e, ErrorCode.AI_BOT_VECTOR_UPSERT_FAILED);
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
			throw translate(e, ErrorCode.AI_BOT_VECTOR_DELETE_FAILED);
		}
	}

	/* ===== Knowledge ===== */

	/**
	 * 지식 등록 — AI 서비스가 청킹해서 청크별 row 생성. 응답은 생성된 청크 리스트.
	 */
	public List<KnowledgeResponse> createKnowledge(
			Long botId,
			String content,
			Provider provider,
			Integer chunkSize,
			Integer chunkOverlap,
			ChunkSplitter chunkSplitter
	) {
		String splitter = chunkSplitter == null ? null : chunkSplitter.name().toLowerCase();
		var body = new KnowledgeCreateRequest(
				botId, content, provider.toAiServiceValue(),
				chunkSize, chunkOverlap, splitter
		);
		try {
			return aiServiceWebClient.post()
					.uri("/knowledge")
					.bodyValue(body)
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<KnowledgeResponse>>() {})
					.block();
		} catch (WebClientResponseException e) {
			throw translate(e, ErrorCode.AI_KNOWLEDGE_CREATE_FAILED);
		}
	}

	/**
	 * 청킹 미리보기 — DB 저장 X, 분할 결과만 반환.
	 * AI 서비스가 미구현 (404) 이면 BAD_GATEWAY 로 변환되어 클라이언트가 fallback 가능.
	 */
	public KnowledgePreviewResponse previewKnowledge(
			String content,
			Integer chunkSize,
			Integer chunkOverlap,
			ChunkSplitter chunkSplitter
	) {
		String splitter = chunkSplitter == null ? null : chunkSplitter.name().toLowerCase();
		var body = new KnowledgePreviewRequest(content, chunkSize, chunkOverlap, splitter);
		try {
			return aiServiceWebClient.post()
					.uri("/knowledge/preview")
					.bodyValue(body)
					.retrieve()
					.bodyToMono(KnowledgePreviewResponse.class)
					.block();
		} catch (WebClientResponseException e) {
			throw translate(e, ErrorCode.AI_KNOWLEDGE_PREVIEW_FAILED);
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
			throw translate(e, ErrorCode.AI_KNOWLEDGE_LIST_FAILED);
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
			throw translate(e, ErrorCode.AI_KNOWLEDGE_DELETE_FAILED);
		}
	}

	private BusinessException translate(WebClientResponseException e, ErrorCode defaultCode) {
		HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
		log.warn("{} - status={}, body={}", defaultCode.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
		// 4xx는 그대로 전파, 5xx는 ErrorCode 기본값(BAD_GATEWAY) 유지
		if (status != null && status.is4xxClientError()) {
			return new BusinessException(defaultCode, status);
		}
		return new BusinessException(defaultCode);
	}
}
