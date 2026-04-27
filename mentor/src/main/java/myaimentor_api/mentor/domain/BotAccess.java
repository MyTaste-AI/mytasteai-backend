package myaimentor_api.mentor.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * 비공개 봇(isPublic=false) 에 대한 사용자별 접근 허용 레코드.
 * (botId, userId) 가 유일. 공개 봇에 대해선 의미 없음 — 공개 봇은 누구나 사용 가능.
 * ADMIN 은 이 테이블과 무관하게 항상 모든 봇 접근 가능.
 */
@Entity
@Table(
		name = "user_bots",
		uniqueConstraints = @UniqueConstraint(name = "uk_user_bots_bot_user", columnNames = {"bot_id", "user_id"}),
		indexes = {
				@Index(name = "idx_user_bots_bot_id", columnList = "bot_id"),
				@Index(name = "idx_user_bots_user_id", columnList = "user_id")
		}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BotAccess {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "bot_id", nullable = false)
	private Long botId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	public BotAccess(Long botId, Long userId) {
		this.botId = botId;
		this.userId = userId;
	}
}
