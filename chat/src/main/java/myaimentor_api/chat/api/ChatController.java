package myaimentor_api.chat.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myaimentor_api.chat.api.dto.ChatRequest;
import myaimentor_api.chat.api.dto.ChatResponse;
import myaimentor_api.chat.api.dto.ChatSessionDetail;
import myaimentor_api.chat.api.dto.ChatSessionSummary;
import myaimentor_api.chat.auth.AuthPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * 채팅 API.
 * - POST /chat                : 질문 송신, 답변 + 세션ID 반환 (히스토리는 Mongo 저장)
 * - GET  /chat/sessions       : 내 세션 목록 (페이징, 최근 업데이트 순)
 * - GET  /chat/sessions/{id}  : 특정 세션의 메시지 전체
 */
@RestController
@RequiredArgsConstructor
public class ChatController {

	private static final String BEARER_PREFIX = "Bearer ";

	private final ChatService chatService;

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

	@GetMapping("/chat/sessions")
	public Page<ChatSessionSummary> listSessions(
			@AuthenticationPrincipal AuthPrincipal principal,
			@PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		Long userId = requireUserId(principal);
		return chatService.listSessions(userId, pageable);
	}

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
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		return principal.userId();
	}

	private String extractBearer(HttpServletRequest http) {
		String header = http.getHeader(HttpHeaders.AUTHORIZATION);
		if (header == null || !header.startsWith(BEARER_PREFIX)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		return header.substring(BEARER_PREFIX.length());
	}
}
