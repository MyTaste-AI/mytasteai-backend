package myaimentor_api.chat.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaimentor_api.chat.external.dto.GenerateRequest;
import myaimentor_api.chat.external.dto.GenerateResponse;
import myaimentor_api.common.error.BusinessException;
import myaimentor_api.common.error.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * AI 마이크로서비스 호출 (chat 모듈 측).
 * - generate: 사용자 질문 → 임베딩 → 봇/지식 검색 → LLM 답변
 *
 * AI 서비스는 별도 인증을 요구하지 않으므로 (내부 네트워크) JWT forward 없음.
 * 4xx 는 원본 status 보존, 5xx/네트워크는 502 (AI_GENERATE_FAILED).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiServiceClient {

	private final @Qualifier("aiWebClient") WebClient aiWebClient;

	/**
	 * AI 답변 생성 — chat 흐름 2단계.
	 * 동기(.block())로 처리해 컨트롤러까지 reactor 타입을 노출하지 않는다.
	 */
	public GenerateResponse generate(GenerateRequest request) {
		try {
			return aiWebClient.post()
					.uri("/generate")
					.bodyValue(request)
					.retrieve()
					.bodyToMono(GenerateResponse.class)
					.block();
		} catch (WebClientResponseException e) {
			log.warn("AI generate 실패 status={} body={}", e.getStatusCode(), e.getResponseBodyAsString());
			HttpStatus s = HttpStatus.resolve(e.getStatusCode().value());
			if (s != null && s.is4xxClientError()) {
				throw new BusinessException(ErrorCode.AI_GENERATE_FAILED, s);
			}
			throw new BusinessException(ErrorCode.AI_GENERATE_FAILED);
		}
	}
}
