package myaimentor_api.mentor.bot;

import myaimentor_api.mentor.bot.dto.BotCreateRequest;
import myaimentor_api.mentor.bot.dto.BotResponse;
import myaimentor_api.mentor.bot.dto.BotSummaryResponse;
import myaimentor_api.mentor.bot.dto.BotUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 봇 서비스 — DB CRUD 와 AI 서비스 라우팅 벡터 동기화를 같이 책임진다.
 */
public interface BotService {

	/**
	 * @param userId  현재 호출자 ID
	 * @param isAdmin true 면 권한 필터 우회 (모든 봇 조회)
	 */
	Page<BotSummaryResponse> findAll(Long categoryId, Pageable pageable, Long userId, boolean isAdmin);

	/**
	 * @param userId  현재 호출자 ID
	 * @param isAdmin true 면 권한 검증 우회
	 *                false 인 경우 비공개 봇이고 allowlist 에 없으면 404 (존재 숨김)
	 */
	BotResponse findById(Long id, Long userId, boolean isAdmin);

	Long create(BotCreateRequest request, Long createdBy);

	BotResponse update(Long id, BotUpdateRequest request);

	void delete(Long id);
}
