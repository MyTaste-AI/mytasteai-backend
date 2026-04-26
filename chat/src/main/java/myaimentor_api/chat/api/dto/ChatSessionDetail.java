package myaimentor_api.chat.api.dto;

import myaimentor_api.chat.domain.ChatMessage;
import myaimentor_api.chat.domain.ChatSession;
import myaimentor_api.chat.domain.MessageRole;

import java.time.Instant;
import java.util.List;

/**
 * GET /chat/sessions/{id} 응답 — 메시지 전체 포함.
 */
public record ChatSessionDetail(
		String id,
		Long botId,
		String title,
		List<MessageView> messages,
		Instant createdAt,
		Instant updatedAt
) {
	public record MessageView(MessageRole role, String content, Instant createdAt) {
		static MessageView from(ChatMessage m) {
			return new MessageView(m.getRole(), m.getContent(), m.getCreatedAt());
		}
	}

	public static ChatSessionDetail from(ChatSession s) {
		List<MessageView> views = s.getMessages() == null ? List.of()
				: s.getMessages().stream().map(MessageView::from).toList();
		return new ChatSessionDetail(s.getId(), s.getBotId(), s.getTitle(), views, s.getCreatedAt(), s.getUpdatedAt());
	}
}
