package myaimentor_api.mentor.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaimentor_api.mentor.ai.AiServiceClient;
import myaimentor_api.mentor.bot.dto.BotCreateRequest;
import myaimentor_api.mentor.bot.dto.BotResponse;
import myaimentor_api.mentor.bot.dto.BotSummaryResponse;
import myaimentor_api.mentor.bot.dto.BotUpdateRequest;
import myaimentor_api.mentor.domain.Bot;
import myaimentor_api.mentor.domain.BotRepository;
import myaimentor_api.mentor.domain.CategoryRepository;
import myaimentor_api.mentor.domain.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotServiceImpl implements BotService {

	private final BotRepository botRepository;
	private final CategoryRepository categoryRepository;
	private final AiServiceClient aiServiceClient;

	@Override
	@Transactional(readOnly = true)
	public Page<BotSummaryResponse> findAll(Long categoryId, Pageable pageable) {
		Page<Bot> page = (categoryId == null)
				? botRepository.findAll(pageable)
				: botRepository.findAllByCategoryId(categoryId, pageable);
		return page.map(BotSummaryResponse::from);
	}

	@Override
	@Transactional(readOnly = true)
	public BotResponse findById(Long id) {
		Bot bot = botRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "봇을 찾을 수 없습니다."));
		return BotResponse.from(bot);
	}

	@Override
	@Transactional
	public Long create(BotCreateRequest request, Long createdBy) {
		if (!categoryRepository.existsById(request.categoryId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 카테고리입니다.");
		}
		Bot saved = botRepository.save(Bot.builder()
				.name(request.name())
				.description(request.description())
				.systemPrompt(request.systemPrompt())
				.provider(request.provider())
				.categoryId(request.categoryId())
				.createdBy(createdBy)
				.build());

		// AI 서비스에 라우팅 벡터 등록 — 실패하면 트랜잭션 롤백
		aiServiceClient.upsertBotVector(saved.getId(), saved.getDescription(), saved.getProvider());
		return saved.getId();
	}

	@Override
	@Transactional
	public BotResponse update(Long id, BotUpdateRequest request) {
		Bot bot = botRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "봇을 찾을 수 없습니다."));

		if (request.categoryId() != null && !categoryRepository.existsById(request.categoryId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 카테고리입니다.");
		}

		Provider previousProvider = bot.getProvider();
		boolean aiResyncNeeded = bot.update(
				request.name(), request.description(), request.systemPrompt(),
				request.provider(), request.categoryId()
		);

		if (aiResyncNeeded) {
			// provider 가 바뀐 경우 이전 provider 의 벡터를 정리하고 새 provider 로 재등록
			if (request.provider() != null && request.provider() != previousProvider) {
				aiServiceClient.deleteBotVector(bot.getId(), previousProvider);
			}
			aiServiceClient.upsertBotVector(bot.getId(), bot.getDescription(), bot.getProvider());
		}
		return BotResponse.from(bot);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		Bot bot = botRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "봇을 찾을 수 없습니다."));
		Provider provider = bot.getProvider();
		botRepository.delete(bot);
		// AI 측 벡터 삭제는 DB 트랜잭션 끝에 시도. 이미 없는 경우는 client 가 삼킨다.
		aiServiceClient.deleteBotVector(id, provider);
	}
}
