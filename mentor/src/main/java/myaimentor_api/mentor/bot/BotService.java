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

	Page<BotSummaryResponse> findAll(Long categoryId, Pageable pageable);

	BotResponse findById(Long id);

	Long create(BotCreateRequest request, Long createdBy);

	BotResponse update(Long id, BotUpdateRequest request);

	void delete(Long id);
}
