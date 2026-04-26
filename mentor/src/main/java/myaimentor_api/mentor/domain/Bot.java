package myaimentor_api.mentor.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * 봇 엔티티.
 * - description: AI 서비스의 라우팅 임베딩에 사용되는 긴 설명
 * - systemPrompt: LLM 호출 시 전달되는 페르소나/규칙
 * - provider: 봇별로 고정되는 LLM (OPENAI / GEMINI)
 * AI 서비스의 bot_vectors_* / bot_knowledge_* 테이블은 이 엔티티의 id를 그대로 bot_id로 사용한다.
 */
@Entity
@Table(
		name = "bots",
		indexes = {
				@Index(name = "idx_bots_category_id", columnList = "category_id")
		}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(name = "system_prompt", nullable = false, columnDefinition = "TEXT")
	private String systemPrompt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Provider provider;

	@Column(name = "category_id", nullable = false)
	private Long categoryId;

	@Column(name = "created_by", nullable = false)
	private Long createdBy;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private Instant updatedAt;

	@Builder
	private Bot(String name, String description, String systemPrompt, Provider provider, Long categoryId, Long createdBy) {
		this.name = name;
		this.description = description;
		this.systemPrompt = systemPrompt;
		this.provider = provider;
		this.categoryId = categoryId;
		this.createdBy = createdBy;
	}

	/**
	 * 부분 업데이트. null 인 필드는 변경하지 않는다.
	 * description / provider 가 변경되면 호출자(서비스)에서 AI 서비스 재호출이 필요하다.
	 */
	public boolean update(String name, String description, String systemPrompt, Provider provider, Long categoryId) {
		boolean descriptionChanged = description != null && !description.equals(this.description);
		boolean providerChanged = provider != null && provider != this.provider;
		if (name != null) this.name = name;
		if (description != null) this.description = description;
		if (systemPrompt != null) this.systemPrompt = systemPrompt;
		if (provider != null) this.provider = provider;
		if (categoryId != null) this.categoryId = categoryId;
		return descriptionChanged || providerChanged;
	}
}
