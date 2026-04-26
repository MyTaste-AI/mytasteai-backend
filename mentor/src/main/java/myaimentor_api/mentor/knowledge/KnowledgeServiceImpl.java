package myaimentor_api.mentor.knowledge;

import lombok.RequiredArgsConstructor;
import myaimentor_api.mentor.ai.AiServiceClient;
import myaimentor_api.mentor.ai.dto.KnowledgeResponse;
import myaimentor_api.mentor.domain.Bot;
import myaimentor_api.mentor.domain.BotRepository;
import myaimentor_api.mentor.knowledge.dto.KnowledgeCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
	public KnowledgeResponse create(Long botId, KnowledgeCreateRequest request) {
		Bot bot = requireBot(botId);
		return aiServiceClient.createKnowledge(bot.getId(), request.content(), bot.getProvider());
	}

	@Override
	@Transactional(readOnly = true)
	public void delete(Long botId, Long knowledgeId) {
		Bot bot = requireBot(botId);
		aiServiceClient.deleteKnowledge(knowledgeId, bot.getProvider());
	}

	private Bot requireBot(Long botId) {
		return botRepository.findById(botId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "봇을 찾을 수 없습니다."));
	}
}
