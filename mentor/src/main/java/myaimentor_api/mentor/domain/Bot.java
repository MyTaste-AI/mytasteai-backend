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
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * 봇 엔티티.
 * - description: AI 서비스의 라우팅 임베딩에 사용되는 긴 설명
 * - systemPrompt: LLM 호출 시 전달되는 페르소나/규칙
 * - provider: 봇별로 고정되는 LLM (OPENAI / GEMINI)
 * - searchType / topK / scoreThreshold: 채팅 시 지식 검색 동작 (RAG 검색)
 * - chunkSize / chunkOverlap / chunkSplitter: 지식 등록 시 텍스트 분할 동작 (RAG 인덱싱)
 *
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

	/** 새 봇 기본값. */
	public static final SearchType DEFAULT_SEARCH_TYPE = SearchType.VECTOR;
	public static final int DEFAULT_TOP_K = 3;
	public static final double DEFAULT_SCORE_THRESHOLD = 0.5;
	public static final int DEFAULT_CHUNK_SIZE = 500;
	public static final int DEFAULT_CHUNK_OVERLAP = 100;
	public static final ChunkSplitter DEFAULT_CHUNK_SPLITTER = ChunkSplitter.RECURSIVE;
	public static final boolean DEFAULT_IS_PUBLIC = true;

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

	/**
	 * 공개 봇 여부. true 면 모든 사용자가 사용 가능, false 면 BotAccess 에 등록된 사용자만 사용 가능.
	 * ADMIN 은 항상 우회.
	 */
	@Column(name = "is_public", nullable = false)
	@ColumnDefault("true")
	private boolean isPublic;

	/* ===== RAG 검색 설정 (채팅 시 retrieval) ===== */

	@Enumerated(EnumType.STRING)
	@Column(name = "search_type", nullable = false, length = 20)
	private SearchType searchType;

	@Column(name = "top_k", nullable = false)
	private Integer topK;

	@Column(name = "score_threshold", nullable = false)
	private Double scoreThreshold;

	/* ===== RAG 청킹 설정 (지식 등록 시 분할) ===== */

	/** 청크 크기 (자 단위, 100~2000 권장) */
	@Column(name = "chunk_size", nullable = false)
	private Integer chunkSize;

	/** 청크 간 오버랩 (보통 chunkSize 의 10~20%) */
	@Column(name = "chunk_overlap", nullable = false)
	private Integer chunkOverlap;

	@Enumerated(EnumType.STRING)
	@Column(name = "chunk_splitter", nullable = false, length = 20)
	private ChunkSplitter chunkSplitter;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private Instant updatedAt;

	@Builder
	private Bot(
			String name,
			String description,
			String systemPrompt,
			Provider provider,
			Long categoryId,
			Long createdBy,
			SearchType searchType,
			Integer topK,
			Double scoreThreshold,
			Integer chunkSize,
			Integer chunkOverlap,
			ChunkSplitter chunkSplitter,
			Boolean isPublic
	) {
		this.name = name;
		this.description = description;
		this.systemPrompt = systemPrompt;
		this.provider = provider;
		this.categoryId = categoryId;
		this.createdBy = createdBy;
		this.searchType = searchType != null ? searchType : DEFAULT_SEARCH_TYPE;
		this.topK = topK != null ? topK : DEFAULT_TOP_K;
		this.scoreThreshold = scoreThreshold != null ? scoreThreshold : DEFAULT_SCORE_THRESHOLD;
		this.chunkSize = chunkSize != null ? chunkSize : DEFAULT_CHUNK_SIZE;
		this.chunkOverlap = chunkOverlap != null ? chunkOverlap : DEFAULT_CHUNK_OVERLAP;
		this.chunkSplitter = chunkSplitter != null ? chunkSplitter : DEFAULT_CHUNK_SPLITTER;
		this.isPublic = isPublic != null ? isPublic : DEFAULT_IS_PUBLIC;
	}

	/**
	 * 부분 업데이트. null 인 필드는 변경하지 않는다.
	 * description / provider 가 변경되면 AI 서비스 재호출 필요 → true 반환.
	 */
	public boolean update(
			String name,
			String description,
			String systemPrompt,
			Provider provider,
			Long categoryId,
			SearchType searchType,
			Integer topK,
			Double scoreThreshold,
			Integer chunkSize,
			Integer chunkOverlap,
			ChunkSplitter chunkSplitter,
			Boolean isPublic
	) {
		boolean descriptionChanged = description != null && !description.equals(this.description);
		boolean providerChanged = provider != null && provider != this.provider;

		if (name != null) this.name = name;
		if (description != null) this.description = description;
		if (systemPrompt != null) this.systemPrompt = systemPrompt;
		if (provider != null) this.provider = provider;
		if (categoryId != null) this.categoryId = categoryId;
		if (searchType != null) this.searchType = searchType;
		if (topK != null) this.topK = topK;
		if (scoreThreshold != null) this.scoreThreshold = scoreThreshold;
		if (chunkSize != null) this.chunkSize = chunkSize;
		if (chunkOverlap != null) this.chunkOverlap = chunkOverlap;
		if (chunkSplitter != null) this.chunkSplitter = chunkSplitter;
		if (isPublic != null) this.isPublic = isPublic;

		return descriptionChanged || providerChanged;
	}
}
