package myaimentor_api.chat.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 세션 도큐먼트 안에 임베딩되는 단일 메시지.
 * 별도 컬렉션으로 분리하지 않는 이유: 한 번에 세션 통째 조회가 자연스럽고 메시지 수가 많지 않음.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatMessage {
	private MessageRole role;
	private String content;
	private Instant createdAt;

	public static ChatMessage user(String content) {
		return new ChatMessage(MessageRole.USER, content, Instant.now());
	}

	public static ChatMessage assistant(String content) {
		return new ChatMessage(MessageRole.ASSISTANT, content, Instant.now());
	}
}
