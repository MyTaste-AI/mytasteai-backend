package myaimentor_api.chat.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaimentor_api.chat.external.dto.GenerateRequest;
import myaimentor_api.chat.external.dto.GenerateResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

/**
 * AI 마이크로서비스 호출.
 * - generate: 사용자 질문 → 임베딩 → 봇/지식 검색 → LLM 답변
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiServiceClient {

	private final @Qualifier("aiWebClient") WebClient aiWebClient;

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
				throw new ResponseStatusException(s, "AI generate 실패");
			}
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI generate 실패");
		}
	}
}
