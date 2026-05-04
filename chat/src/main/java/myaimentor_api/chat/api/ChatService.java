package myaimentor_api.chat.api;

import myaimentor_api.chat.api.dto.ChatRequest;
import myaimentor_api.chat.api.dto.ChatResponse;
import myaimentor_api.chat.api.dto.ChatSessionDetail;
import myaimentor_api.chat.api.dto.ChatSessionSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 채팅 서비스 — 봇 정보 조회 → AI generate 호출 → MongoDB 세션 누적.
 */
public interface ChatService {

	/**
	 * @param userId      인증된 사용자 ID
	 * @param bearerToken mentor 서비스에 봇 조회 호출 시 forward 할 토큰
	 */
	ChatResponse chat(Long userId, String bearerToken, ChatRequest request);

	Page<ChatSessionSummary> listSessions(Long userId, Pageable pageable);

	ChatSessionDetail getSession(Long userId, String sessionId);

	void deleteSession(Long userId, String sessionId);
}
