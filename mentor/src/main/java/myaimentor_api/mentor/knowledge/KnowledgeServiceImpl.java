package myaimentor_api.mentor.knowledge;

import lombok.RequiredArgsConstructor;
import myaimentor_api.common.error.BusinessException;
import myaimentor_api.common.error.ErrorCode;
import myaimentor_api.mentor.ai.AiServiceClient;
import myaimentor_api.mentor.ai.dto.KnowledgeResponse;
import myaimentor_api.mentor.domain.Bot;
import myaimentor_api.mentor.domain.BotRepository;
import myaimentor_api.mentor.knowledge.dto.KnowledgeCreateRequest;
import myaimentor_api.mentor.knowledge.dto.KnowledgePreviewResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 봇 지식 서비스 구현 — Spring DB 에 별도 저장 없이 AI 서비스로 그대로 위임.
 * Spring 측 책임은 두 가지뿐:
 *  1) 봇 존재 여부 확인 (없으면 404 BOT-001)
 *  2) 봇의 provider 결정 → AI 호출 시 provider 파라미터로 전달
 *
 * 응답 스키마(KnowledgeResponse)는 AI 서비스 그대로 — Spring 에서 가공하지 않는다.
 */
@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

	private final BotRepository botRepository;
	private final AiServiceClient aiServiceClient;

	@Override
	@Transactional(readOnly = true)
	public List<KnowledgeResponse> list(Long botId, int limit, int offset) {
		Bot bot = requireBot(botId);
		return aiServiceClient.listKnowledge(bot.getId(), bot.getProvider(), limit, offset);
	}

	@Override
	@Transactional(readOnly = true)
	public List<KnowledgeResponse> create(Long botId, KnowledgeCreateRequest request) {
		Bot bot = requireBot(botId);
		// 봇 설정의 청킹 파라미터를 AI 서비스에 전달 → AI 가 청크 분할 + 임베딩 + 저장
		// 응답은 생성된 청크 리스트 (1 등록 → N row).
		return aiServiceClient.createKnowledge(
				bot.getId(),
				request.content(),
				bot.getProvider(),
				bot.getChunkSize(),
				bot.getChunkOverlap(),
				bot.getChunkSplitter()
		);
	}

	@Override
	@Transactional(readOnly = true)
	public void delete(Long botId, Long knowledgeId) {
		Bot bot = requireBot(botId);
		aiServiceClient.deleteKnowledge(knowledgeId, bot.getProvider());
	}

	@Override
	@Transactional(readOnly = true)
	public KnowledgePreviewResponse preview(Long botId, String content) {
		Bot bot = requireBot(botId);
		return aiServiceClient.previewKnowledge(
				content,
				bot.getChunkSize(),
				bot.getChunkOverlap(),
				bot.getChunkSplitter()
		);
	}

	private Bot requireBot(Long botId) {
		return botRepository.findById(botId)
				.orElseThrow(() -> new BusinessException(ErrorCode.BOT_NOT_FOUND));
	}
}
