package myaimentor_api.mentor.knowledge;

import myaimentor_api.mentor.ai.dto.KnowledgeResponse;
import myaimentor_api.mentor.knowledge.dto.KnowledgeCreateRequest;
import myaimentor_api.mentor.knowledge.dto.KnowledgePreviewResponse;

import java.util.List;

/**
 * 봇 지식 서비스 — Spring DB 에는 별도로 저장하지 않고 AI 서비스에 위임.
 * 봇 존재 여부와 provider 결정만 Spring 에서 처리.
 */
public interface KnowledgeService {

	List<KnowledgeResponse> list(Long botId, int limit, int offset);

	List<KnowledgeResponse> create(Long botId, KnowledgeCreateRequest request);

	void delete(Long botId, Long knowledgeId);

	/**
	 * 봇의 청킹 설정으로 미리보기 — 저장 없음.
	 * AI 서비스가 미구현이면 BAD_GATEWAY (EXT-008) 로 응답되어 프론트가 fallback.
	 */
	KnowledgePreviewResponse preview(Long botId, String content);
}
