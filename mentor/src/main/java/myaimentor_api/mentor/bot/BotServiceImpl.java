package myaimentor_api.mentor.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaimentor_api.common.error.BusinessException;
import myaimentor_api.common.error.ErrorCode;
import myaimentor_api.mentor.ai.AiServiceClient;
import myaimentor_api.mentor.bot.dto.BotCreateRequest;
import myaimentor_api.mentor.bot.dto.BotResponse;
import myaimentor_api.mentor.bot.dto.BotSummaryResponse;
import myaimentor_api.mentor.bot.dto.BotUpdateRequest;
import myaimentor_api.mentor.domain.Bot;
import myaimentor_api.mentor.domain.BotAccessRepository;
import myaimentor_api.mentor.domain.BotRepository;
import myaimentor_api.mentor.domain.CategoryRepository;
import myaimentor_api.mentor.domain.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 봇 서비스 구현 — Spring DB CRUD + AI 마이크로서비스(bot-vectors)의 동기화를 함께 책임진다.
 *
 * 핵심 규칙:
 *  - create:  카테고리 검증 → DB 저장 → AI upsert (실패 시 트랜잭션 롤백)
 *  - update:  description/provider 변경 시에만 AI 재호출.
 *             provider 가 바뀌면 이전 provider 의 벡터를 먼저 삭제 후 새로 등록.
 *  - delete:  DB 삭제 → AI 측 정리 (이미 없으면 client 가 swallow → 멱등)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotServiceImpl implements BotService {

	private final BotRepository botRepository;
	private final CategoryRepository categoryRepository;
	private final BotAccessRepository botAccessRepository;
	private final AiServiceClient aiServiceClient;

	@Override
	@Transactional(readOnly = true)
	public Page<BotSummaryResponse> findAll(Long categoryId, Pageable pageable, Long userId, boolean isAdmin) {
		Page<Bot> page;
		if (isAdmin) {
			page = (categoryId == null)
					? botRepository.findAll(pageable)
					: botRepository.findAllByCategoryId(categoryId, pageable);
		} else {
			page = (categoryId == null)
					? botRepository.findAllAccessibleByUser(userId, pageable)
					: botRepository.findAllAccessibleByUserAndCategoryId(userId, categoryId, pageable);
		}
		return page.map(BotSummaryResponse::from);
	}

	@Override
	@Transactional(readOnly = true)
	public BotResponse findById(Long id, Long userId, boolean isAdmin) {
		Bot bot = botRepository.findById(id)
				.orElseThrow(() -> new BusinessException(ErrorCode.BOT_NOT_FOUND));

		// 비-ADMIN: 비공개 봇이고 allowlist 에 없으면 존재를 숨겨 404 처리
		if (!isAdmin && !bot.isPublic() && !botAccessRepository.existsByBotIdAndUserId(id, userId)) {
			throw new BusinessException(ErrorCode.BOT_NOT_FOUND);
		}
		return BotResponse.from(bot);
	}

	@Override
	@Transactional
	public Long create(BotCreateRequest request, Long createdBy) {
		if (!categoryRepository.existsById(request.categoryId())) {
			throw new BusinessException(ErrorCode.CATEGORY_INVALID);
		}
		Bot saved = botRepository.save(Bot.builder()
				.name(request.name())
				.description(request.description())
				.systemPrompt(request.systemPrompt())
				.provider(request.provider())
				.categoryId(request.categoryId())
				.createdBy(createdBy)
				.searchType(request.searchType())
				.topK(request.topK())
				.scoreThreshold(request.scoreThreshold())
				.chunkSize(request.chunkSize())
				.chunkOverlap(request.chunkOverlap())
				.chunkSplitter(request.chunkSplitter())
				.isPublic(request.isPublic())
				.build());

		// AI 서비스에 라우팅 벡터 등록 — 실패하면 트랜잭션 롤백
		aiServiceClient.upsertBotVector(saved.getId(), saved.getDescription(), saved.getProvider());
		return saved.getId();
	}

	@Override
	@Transactional
	public BotResponse update(Long id, BotUpdateRequest request) {
		Bot bot = botRepository.findById(id)
				.orElseThrow(() -> new BusinessException(ErrorCode.BOT_NOT_FOUND));

		if (request.categoryId() != null && !categoryRepository.existsById(request.categoryId())) {
			throw new BusinessException(ErrorCode.CATEGORY_INVALID);
		}

		Provider previousProvider = bot.getProvider();
		boolean aiResyncNeeded = bot.update(
				request.name(), request.description(), request.systemPrompt(),
				request.provider(), request.categoryId(),
				request.searchType(), request.topK(), request.scoreThreshold(),
				request.chunkSize(), request.chunkOverlap(), request.chunkSplitter(),
				request.isPublic()
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
				.orElseThrow(() -> new BusinessException(ErrorCode.BOT_NOT_FOUND));
		Provider provider = bot.getProvider();
		botAccessRepository.deleteAllByBotId(id);
		botRepository.delete(bot);
		// AI 측 벡터 삭제는 DB 트랜잭션 끝에 시도. 이미 없는 경우는 client 가 삼킨다.
		aiServiceClient.deleteBotVector(id, provider);
	}
}
