package myaimentor_api.chat.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaimentor_api.chat.external.dto.BotInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

/**
 * mentor 서비스에서 봇 정보를 가져오는 클라이언트.
 * 사용자 JWT 를 그대로 forward 해서 mentor 의 인증 필터를 통과시킨다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MentorClient {

	private final @Qualifier("mentorWebClient") WebClient mentorWebClient;

	public BotInfo findBot(Long botId, String bearerToken) {
		try {
			return mentorWebClient.get()
					.uri("/bots/{id}", botId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
					.retrieve()
					.bodyToMono(BotInfo.class)
					.block();
		} catch (WebClientResponseException.NotFound e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "봇을 찾을 수 없습니다.");
		} catch (WebClientResponseException e) {
			log.warn("mentor 봇 조회 실패 status={} body={}", e.getStatusCode(), e.getResponseBodyAsString());
			HttpStatus s = HttpStatus.resolve(e.getStatusCode().value());
			if (s != null && s.is4xxClientError()) {
				throw new ResponseStatusException(s, "mentor 봇 조회 실패");
			}
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "mentor 봇 조회 실패");
		}
	}
}
