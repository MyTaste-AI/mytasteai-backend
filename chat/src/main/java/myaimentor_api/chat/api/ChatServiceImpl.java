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
import myaimentor_api.common.error.BusinessException;
import myaimentor_api.common.error.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 채팅 서비스 구현 — 한 번의 chat() 호출이 다음 4단계를 책임진다:
 *
 *   1) mentor 서비스에서 봇 메타(provider/systemPrompt 등) 조회
 *   2) AI 서비스에 generate 요청 → 답변 수신
 *   3) sessionId 유무에 따라 ChatSession 신규 생성 또는 본인 세션 로드
 *   4) (질문, 답변) 쌍을 메시지로 누적 후 Mongo 저장
 *
 * 외부 호출이 실패하면 BusinessException 으로 변환되어 GlobalExceptionHandler 가 502 응답.
 * 본인 세션이 아닌 ID 로 접근 시 존재를 숨기고 404 (보안).
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

	/** 첫 질문에서 자동 생성되는 세션 제목의 최대 길이 — 초과 시 말줄임표. */
	private static final int TITLE_MAX = 30;

	private final ChatSessionRepository sessionRepository;
	private final MentorClient mentorClient;
	private final AiServiceClient aiServiceClient;

	@Override
	public ChatResponse chat(Long userId, String bearerToken, ChatRequest request) {
		// 1) 봇 정보 조회 (mentor)
		BotInfo bot = mentorClient.findBot(request.botId(), bearerToken);

		// 2) AI 답변 생성 — provider 는 mentor 응답의 enum 명(OPENAI/GEMINI) → 소문자로
		// 봇 설정의 검색 파라미터(searchType/topK/scoreThreshold) 도 함께 전달 →
		// AI 서비스가 RAG 검색에 활용 (미지원이면 무시).
		String providerLower = bot.provider().toLowerCase();
		String searchTypeLower = bot.searchType() == null ? null : bot.searchType().toLowerCase();
		GenerateResponse aiResp = aiServiceClient.generate(new GenerateRequest(
				request.question(),
				providerLower,
				bot.id(),
				bot.systemPrompt(),
				searchTypeLower,
				bot.topK(),
				bot.scoreThreshold()
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

	@Override
	public void deleteSession(Long userId, String sessionId) {
		ChatSession session = loadOwned(userId, sessionId);
		sessionRepository.delete(session);
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
				.orElseThrow(() -> new BusinessException(ErrorCode.CHAT_SESSION_NOT_FOUND));
		if (!s.getUserId().equals(userId)) {
			// 본인 세션이 아니면 존재 자체를 숨김 — 404 로 응답
			throw new BusinessException(ErrorCode.CHAT_SESSION_NOT_FOUND);
		}
		return s;
	}
}
