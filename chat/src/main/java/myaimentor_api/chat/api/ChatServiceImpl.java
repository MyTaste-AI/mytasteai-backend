package myaimentor_api.chat.api;

import lombok.RequiredArgsConstructor;
import myaimentor_api.chat.api.dto.ChatRequest;
import myaimentor_api.chat.api.dto.ChatResponse;
import myaimentor_api.chat.api.dto.ChatSessionDetail;
import myaimentor_api.chat.api.dto.ChatSessionSummary;
import myaimentor_api.chat.domain.ChatSession;
import myaimentor_api.chat.domain.ChatSessionRepository;
import myaimentor_api.chat.external.AiServiceClient;
import myaimentor_api.chat.external.MentorClient;
import myaimentor_api.chat.external.dto.BotInfo;
import myaimentor_api.chat.external.dto.GenerateRequest;
import myaimentor_api.chat.external.dto.GenerateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

	private static final int TITLE_MAX = 30;

	private final ChatSessionRepository sessionRepository;
	private final MentorClient mentorClient;
	private final AiServiceClient aiServiceClient;

	@Override
	public ChatResponse chat(Long userId, String bearerToken, ChatRequest request) {
		// 1) 봇 정보 조회 (mentor)
		BotInfo bot = mentorClient.findBot(request.botId(), bearerToken);

		// 2) AI 답변 생성 — provider 는 mentor 응답의 enum 명(OPENAI/GEMINI) → 소문자로
		String providerLower = bot.provider().toLowerCase();
		GenerateResponse aiResp = aiServiceClient.generate(new GenerateRequest(
				request.question(),
				providerLower,
				bot.id(),
				bot.systemPrompt(),
				null
		));

		// 3) 세션 로드 또는 신규
		ChatSession session = (request.sessionId() == null)
				? newSession(userId, bot, request.question())
				: loadOwned(userId, request.sessionId());

		// 4) 메시지 누적 후 저장
		session.addExchange(request.question(), aiResp.answer());
		ChatSession saved = sessionRepository.save(session);

		return new ChatResponse(saved.getId(), bot.id(), aiResp.answer(), aiResp.mode());
	}

	@Override
	public Page<ChatSessionSummary> listSessions(Long userId, Pageable pageable) {
		return sessionRepository.findAllByUserId(userId, pageable).map(ChatSessionSummary::from);
	}

	@Override
	public ChatSessionDetail getSession(Long userId, String sessionId) {
		return ChatSessionDetail.from(loadOwned(userId, sessionId));
	}

	private ChatSession newSession(Long userId, BotInfo bot, String firstQuestion) {
		String title = firstQuestion.length() > TITLE_MAX
				? firstQuestion.substring(0, TITLE_MAX) + "…"
				: firstQuestion;
		return ChatSession.builder()
				.userId(userId)
				.botId(bot.id())
				.title(title)
				.build();
	}

	private ChatSession loadOwned(Long userId, String sessionId) {
		ChatSession s = sessionRepository.findById(sessionId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다."));
		if (!s.getUserId().equals(userId)) {
			// 본인 세션이 아니면 존재 자체를 숨김 — 404 로 응답
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다.");
		}
		return s;
	}
}
