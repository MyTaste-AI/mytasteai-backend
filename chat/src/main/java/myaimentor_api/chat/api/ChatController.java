package myaimentor_api.chat.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myaimentor_api.chat.api.dto.ChatRequest;
import myaimentor_api.chat.api.dto.ChatResponse;
import myaimentor_api.chat.api.dto.ChatSessionDetail;
import myaimentor_api.chat.api.dto.ChatSessionSummary;
import myaimentor_api.common.auth.AuthPrincipal;
import myaimentor_api.common.error.BusinessException;
import myaimentor_api.common.error.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 채팅 API.
 * - POST /chat                : 질문 송신, 답변 + 세션ID 반환 (히스토리는 Mongo 저장)
 * - GET  /chat/sessions       : 내 세션 목록 (페이징, 최근 업데이트 순)
 * - GET  /chat/sessions/{id}  : 특정 세션의 메시지 전체
 *
 * 모든 엔드포인트는 인증 필수. 본인이 생성한 세션만 조회 가능 (다른 사용자의 세션은 404).
 */
@RestController
@RequiredArgsConstructor
public class ChatController {

	private static final String BEARER_PREFIX = "Bearer ";

	private final ChatService chatService;

	/**
	 * 질문 송신 및 답변 생성
	 * POST /chat
	 *
	 * 요청 body 의 botId/question 으로 AI 답변을 생성하고 세션에 누적 저장한다.
	 * sessionId 가 있으면 기존 세션에 이어쓰고, 없으면 새 세션 생성.
	 *
	 * 다운스트림(mentor 봇 조회)에 호출자의 JWT 를 forward 해야 하므로 Authorization 헤더를 직접 추출한다.
	 * - 봇 미존재 시 404 (BOT-001)
	 * - 본인 세션이 아닌 sessionId 지정 시 404 (CHAT-001)
	 * - AI/mentor 호출 실패 시 502 (EXT-001/002)
	 */
	@PostMapping("/chat")
	public ChatResponse chat(
			@AuthenticationPrincipal AuthPrincipal principal,
			@RequestBody @Valid ChatRequest request,
			HttpServletRequest http
	) {
		Long userId = requireUserId(principal);
		String token = extractBearer(http);
		return chatService.chat(userId, token, request);
	}

	/**
	 * 내 세션 목록
	 * GET /chat/sessions?page=0&size=20
	 *
	 * 본인 세션만 페이징 반환. 기본 정렬은 updatedAt DESC (최근 활동 순).
	 * 응답은 메시지 본문 없이 요약(title/last 등)만 포함 — 상세는 /chat/sessions/{id}.
	 */
	@GetMapping("/chat/sessions")
	public Page<ChatSessionSummary> listSessions(
			@AuthenticationPrincipal AuthPrincipal principal,
			@PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		Long userId = requireUserId(principal);
		return chatService.listSessions(userId, pageable);
	}

	/**
	 * 세션 상세 (메시지 전체)
	 * GET /chat/sessions/{id}
	 *
	 * 본인 세션이 아닌 경우 존재 자체를 숨겨 404 로 응답 (보안 — id 추측 방지).
	 */
	@GetMapping("/chat/sessions/{id}")
	public ChatSessionDetail getSession(
			@AuthenticationPrincipal AuthPrincipal principal,
			@PathVariable String id
	) {
		Long userId = requireUserId(principal);
		return chatService.getSession(userId, id);
	}

	private Long requireUserId(AuthPrincipal principal) {
		if (principal == null) {
			throw new BusinessException(ErrorCode.AUTH_REQUIRED);
		}
		return principal.userId();
	}

	private String extractBearer(HttpServletRequest http) {
		String header = http.getHeader(HttpHeaders.AUTHORIZATION);
		if (header == null || !header.startsWith(BEARER_PREFIX)) {
			throw new BusinessException(ErrorCode.AUTH_REQUIRED);
		}
		return header.substring(BEARER_PREFIX.length());
	}
}
