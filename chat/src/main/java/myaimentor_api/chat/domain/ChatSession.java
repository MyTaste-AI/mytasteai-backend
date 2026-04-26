package myaimentor_api.chat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자별 봇 채팅 세션. messages 는 임베딩 — 한 번 조회로 전체 대화 로드.
 * 앞으로 메시지 수가 매우 많아지면 별도 컬렉션 분리 고려.
 */
@Document(collection = "chat_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatSession {

	@Id
	private String id;

	@Indexed
	private Long userId;

	@Indexed
	private Long botId;

	private String title;

	private List<ChatMessage> messages;

	private Instant createdAt;

	private Instant updatedAt;

	@Builder
	private ChatSession(Long userId, Long botId, String title) {
		this.userId = userId;
		this.botId = botId;
		this.title = title;
		this.messages = new ArrayList<>();
		Instant now = Instant.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	public void addExchange(String userQuestion, String assistantAnswer) {
		this.messages.add(ChatMessage.user(userQuestion));
		this.messages.add(ChatMessage.assistant(assistantAnswer));
		this.updatedAt = Instant.now();
	}
}
