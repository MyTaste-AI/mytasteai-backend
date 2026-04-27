package myaimentor_api.chat.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaimentor_api.chat.external.dto.BotInfo;
import myaimentor_api.common.error.BusinessException;
import myaimentor_api.common.error.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * mentor 서비스에서 봇 정보를 가져오는 클라이언트.
 * 사용자 JWT 를 그대로 forward 해서 mentor 의 인증 필터를 통과시킨다.
 *
 * 에러 매핑:
 *  - 404           → BOT_NOT_FOUND (404)
 *  - 그 외 4xx     → MENTOR_BOT_FETCH_FAILED + 원본 status 보존
 *  - 5xx / 네트워크 → MENTOR_BOT_FETCH_FAILED (502 BadGateway)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MentorClient {

	private final @Qualifier("mentorWebClient") WebClient mentorWebClient;

	/**
	 * 봇 메타(provider/systemPrompt 등) 조회 — chat 흐름 1단계.
	 *
	 * @param botId       조회할 봇 ID
	 * @param bearerToken 호출자의 JWT (Bearer 접두 없이) — mentor 필터 통과용
	 */
	public BotInfo findBot(Long botId, String bearerToken) {
		try {
			return mentorWebClient.get()
					.uri("/bots/{id}", botId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
					.retrieve()
					.bodyToMono(BotInfo.class)
					.block();
		} catch (WebClientResponseException.NotFound e) {
			throw new BusinessException(ErrorCode.BOT_NOT_FOUND);
		} catch (WebClientResponseException e) {
			log.warn("mentor 봇 조회 실패 status={} body={}", e.getStatusCode(), e.getResponseBodyAsString());
			HttpStatus s = HttpStatus.resolve(e.getStatusCode().value());
			// 4xx 면 그대로 전파, 5xx 는 502 (ErrorCode 기본값)
			if (s != null && s.is4xxClientError()) {
				throw new BusinessException(ErrorCode.MENTOR_BOT_FETCH_FAILED, s);
			}
			throw new BusinessException(ErrorCode.MENTOR_BOT_FETCH_FAILED);
		}
	}
}
