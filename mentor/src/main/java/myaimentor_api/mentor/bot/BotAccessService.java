package myaimentor_api.mentor.bot;

import myaimentor_api.mentor.bot.dto.BotAccessResponse;

import java.util.List;

/**
 * 봇별 사용자 접근권 관리. 모든 메서드는 ADMIN 전용 호출이 전제.
 * 공개 봇(isPublic=true) 에 대해선 기록이 의미 없지만, 추후 비공개 전환 대비로 동일하게 저장 허용.
 */
public interface BotAccessService {

	List<BotAccessResponse> list(Long botId);

	/** 멱등 — 이미 등록되어 있으면 no-op. */
	void grant(Long botId, Long userId);

	/** 멱등 — 등록되어 있지 않으면 no-op. */
	void revoke(Long botId, Long userId);
}
