package myaimentor_api.chat.api.dto;

import myaimentor_api.chat.domain.ChatSession;

import java.time.Instant;

/**
 * 세션 목록 응답용 — messages 본문은 제외하고 마지막 메시지 미리보기만.
 */
public record ChatSessionSummary(
		String id,
		Long botId,
		String title,
		int messageCount,
		String lastMessagePreview,
		Instant createdAt,
		Instant updatedAt
) {
	private static final int PREVIEW_MAX = 80;

	public static ChatSessionSummary from(ChatSession s) {
		String preview = null;
		if (s.getMessages() != null && !s.getMessages().isEmpty()) {
			String content = s.getMessages().get(s.getMessages().size() - 1).getContent();
			preview = content.length() > PREVIEW_MAX ? content.substring(0, PREVIEW_MAX) + "…" : content;
		}
		int count = s.getMessages() == null ? 0 : s.getMessages().size();
		return new ChatSessionSummary(s.getId(), s.getBotId(), s.getTitle(), count, preview, s.getCreatedAt(), s.getUpdatedAt());
	}
}
